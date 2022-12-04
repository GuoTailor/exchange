package com.exchange.service;

import com.exchange.domain.Futures;
import com.exchange.domain.TradingTime;
import com.exchange.dto.FuturesAndTime;
import com.exchange.dto.RedisMarket;
import com.exchange.enums.TradingStateEnum;
import com.exchange.exception.BusinessException;
import com.exchange.mapper.FuturesMapper;
import com.exchange.mapper.TradingRecordMapper;
import com.exchange.mapper.TradingTimeMapper;
import com.exchange.schedule.FuturesCloseOutJobInfo;
import com.exchange.schedule.QuartzManager;
import com.exchange.util.ThreadManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * create by GYH on 2022/11/16
 */
@Slf4j
@Service
public class FuturesService implements InitializingBean {
    @Resource
    private FuturesMapper futuresMapper;
    @Resource
    private TradingTimeMapper tradingTimeMapper;
    @Resource
    private TradingRecordMapper tradingRecordMapper;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MarketService marketService;

    @Override
    public void afterPropertiesSet() {
        List<FuturesAndTime> block = getAllAndTime().block();
        if (block != null) {
            Duration startStopDuration = Duration.ofNanos(TradingRecordService.startStopTime.toNanoOfDay());
            for (FuturesAndTime futuresAndTime : block) {
                for (TradingTime tradingTime : futuresAndTime.getTradingTime()) {
                    if (tradingTime.getStartTime().compareTo(startStopDuration) > 0) {
                        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                        // 夜盘收盘前两分钟开始强平
                        LocalDateTime closeOutTime = time.plusSeconds(tradingTime.getEndTime().toSeconds() - 120);
                        quartzManager.addJob(new FuturesCloseOutJobInfo(futuresAndTime.getId(), closeOutTime.toLocalTime()));
                        break;
                    }
                }
            }
        }
    }

    public void closeOut(Integer id) {
        log.info("强平");
        futuresMapper.findById(id)
                .zipWhen(it -> redisTemplate.opsForValue().get(it.getSymbol()).cast(RedisMarket.class))
                .flatMap(it -> tradingRecordMapper.findByStateAndFuturesId(TradingStateEnum.BUY, it.getT1().getId())
                        .flatMap(tradingRecord -> {
                            Futures futures = it.getT1();
                            RedisMarket redisMarket = it.getT2();
                            return marketService.closeOut(futures, redisMarket, tradingRecord);
                        }).then())
                .subscribeOn(Schedulers.fromExecutorService(ThreadManager.getQuickPool().getExecutorService())).subscribe();

    }

    public Mono<List<FuturesAndTime>> getAllAndTime() {
        return futuresMapper.findAll()
                .flatMap(it -> {
                    FuturesAndTime futuresAndTime = new FuturesAndTime();
                    BeanUtils.copyProperties(it, futuresAndTime);
                    return tradingTimeMapper.findByFuturesId(it.getId())
                            .collectList()
                            .map(time -> {
                                futuresAndTime.setTradingTime(time);
                                return futuresAndTime;
                            });
                })
                .collectList();
    }

    public Mono<FuturesAndTime> findByIdAndTime(Integer id) {
        return futuresMapper.findById(id)
                .flatMap(it -> {
                    FuturesAndTime futuresAndTime = new FuturesAndTime();
                    BeanUtils.copyProperties(it, futuresAndTime);
                    return tradingTimeMapper.findByFuturesId(it.getId())
                            .collectList()
                            .map(time -> {
                                futuresAndTime.setTradingTime(time);
                                return futuresAndTime;
                            });
                });
    }

    /**
     * 修改期货，但是不能修改开盘时间
     *
     * @param futures 期货
     * @return 结构
     */
    public Mono<Futures> updateFutures(Futures futures) {
        return futuresMapper.save(futures);
    }

    public Mono<Futures> addFutures(Futures futures) {
        futures.setId(null);
        return futuresMapper.save(futures);
    }


    /**
     * 删除期货
     *
     * @param id 期货id
     * @return 结果
     */
    public Mono<Futures> deleteFutures(Integer id) {
        return futuresMapper.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("没有该期货id " + id)))
                .flatMap(it -> futuresMapper.deleteById(id).then(Mono.just(it)))
                .doOnSuccess(it -> {
                    quartzManager.removeJob(new FuturesCloseOutJobInfo(id));
                    log.info("移除强平时间 {}", id);
                });
    }

}

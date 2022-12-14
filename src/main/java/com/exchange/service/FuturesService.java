package com.exchange.service;

import com.exchange.domain.Futures;
import com.exchange.domain.TradingTime;
import com.exchange.dto.FuturesAndTime;
import com.exchange.dto.RedisMarket;
import com.exchange.dto.req.TradingTimeInstallReq;
import com.exchange.dto.req.TradingTimeUpdateReq;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

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
                        // ????????????????????????????????????
                        LocalDateTime closeOutTime = time.plusSeconds(tradingTime.getEndTime().toSeconds() - 120);
                        quartzManager.addJob(new FuturesCloseOutJobInfo(futuresAndTime.getId(), closeOutTime.toLocalTime()));
                        break;
                    }
                }
            }
        }
    }

    public void closeOut(Integer id) {
        log.info("??????");
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
        return futuresMapper.findAllByEnabled(true)
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
                .flatMap(it -> redisTemplate.opsForValue().get(it.getSymbol())
                        .cast(RedisMarket.class)
                        .map(redis -> {
                            it.setIncrease(redis.getZf().setScale(2, RoundingMode.HALF_UP));
                            return it;
                        }))
                .collectList();
    }

    public Mono<FuturesAndTime> findByIdAndTime(Integer id) {
        return futuresMapper.findByIdAndEnabled(id, true)
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
     * ?????????????????????????????????????????????
     *
     * @param futures ??????
     * @return ??????
     */
    public Mono<Futures> updateFutures(Futures futures) {
        return futuresMapper.save(futures);
    }

    public Mono<Futures> addFutures(Futures futures) {
        futures.setId(null);
        return futuresMapper.save(futures);
    }

    /**
     * ????????????
     *
     * @param id ??????id
     * @return ??????
     */
    public Mono<Futures> deleteFutures(Integer id) {
        return futuresMapper.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("???????????????id " + id)))
                .flatMap(it -> futuresMapper.deleteById(id).then(Mono.just(it)))
                .doOnSuccess(it -> {
                    quartzManager.removeJob(new FuturesCloseOutJobInfo(id));
                    log.info("?????????????????? {}", id);
                });
    }

    /**
     * ???????????????????????????
     */
    public Mono<Tuple2<TradingTime, TradingTime>> updateFuturesTime(TradingTimeUpdateReq timeReq) {
        return tradingTimeMapper.findById(timeReq.getId())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("?????????????????????" + timeReq.getId())))
                .flatMap(it -> futuresMapper.findById(it.getFuturesId())
                        .filter(Objects::nonNull)
                        .switchIfEmpty(Mono.error(new BusinessException("??????????????? id???" + timeReq.getId())))
                        .map(n -> it))
                .zipWhen(it -> {
                    TradingTime time = new TradingTime();
                    BeanUtils.copyProperties(timeReq, time);
                    time.setFuturesId(it.getFuturesId());
                    return tradingTimeMapper.save(time);
                })
                .doOnSuccess(it -> {
                    Duration startStopDuration = Duration.ofNanos(TradingRecordService.startStopTime.toNanoOfDay());
                    if (it.getT1().getStartTime().compareTo(startStopDuration) > 0) {
                        // ????????????????????????????????????????????????????????????
                        if (it.getT2().getStartTime().compareTo(startStopDuration) > 0) {
                            LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                            // ????????????????????????????????????
                            LocalDateTime closeOutTime = time.plusSeconds(it.getT2().getEndTime().toSeconds() - 120);
                            quartzManager.modifyJobTime(new FuturesCloseOutJobInfo(it.getT2().getFuturesId(), closeOutTime.toLocalTime()));
                            log.info("?????????????????? {}", it.getT2().getFuturesId());
                        } else {
                            quartzManager.removeJob(new FuturesCloseOutJobInfo(it.getT2().getFuturesId()));
                            log.info("?????????????????? {}", it.getT2().getFuturesId());
                        }
                    } else {
                        // ????????????????????????????????????????????????????????????
                        if (it.getT2().getStartTime().compareTo(startStopDuration) > 0) {
                            LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                            // ????????????????????????????????????
                            LocalDateTime closeOutTime = time.plusSeconds(it.getT2().getEndTime().toSeconds() - 120);
                            quartzManager.addJob(new FuturesCloseOutJobInfo(it.getT2().getFuturesId(), closeOutTime.toLocalTime()));
                            log.info("?????????????????? {}", it.getT2().getFuturesId());
                        }
                    }
                });
    }

    /**
     * ???????????????????????????
     *
     * @param timeReq ??????????????????
     * @return ??????
     */
    public Mono<TradingTime> addFuturesTime(TradingTimeInstallReq timeReq) {
        return futuresMapper.findById(timeReq.getFuturesId())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("???????????????" + timeReq.getFuturesId())))
                .flatMap(it -> {
                    TradingTime time = new TradingTime();
                    BeanUtils.copyProperties(it, time);
                    return tradingTimeMapper.save(time);
                })
                .doOnSuccess(it -> {
                    Duration startStopDuration = Duration.ofNanos(TradingRecordService.startStopTime.toNanoOfDay());
                    if (it.getStartTime().compareTo(startStopDuration) > 0) {
                        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                        // ????????????????????????????????????
                        LocalDateTime closeOutTime = time.plusSeconds(it.getEndTime().toSeconds() - 120);
                        quartzManager.addJob(new FuturesCloseOutJobInfo(it.getFuturesId(), closeOutTime.toLocalTime()));
                        log.info("?????????????????? {}", it.getId());
                    }
                });
    }

    /**
     * ????????????????????????
     *
     * @param id id
     */
    public Mono<TradingTime> deleteFuturesTime(Integer id) {
        return tradingTimeMapper.findById(id)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("??????????????????" + id)))
                .flatMap(it -> tradingTimeMapper.deleteById(id).then(Mono.just(it)))
                .doOnSuccess(it -> {
                    Duration startStopDuration = Duration.ofNanos(TradingRecordService.startStopTime.toNanoOfDay());
                    if (it.getStartTime().compareTo(startStopDuration) > 0) {
                        quartzManager.removeJob(new FuturesCloseOutJobInfo(it.getFuturesId()));
                        log.info("?????????????????? {}", it.getFuturesId());
                    }
                });
    }

}

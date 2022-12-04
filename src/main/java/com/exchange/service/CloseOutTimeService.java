package com.exchange.service;

import com.exchange.domain.CloseOutTime;
import com.exchange.domain.Futures;
import com.exchange.dto.RedisMarket;
import com.exchange.dto.req.CloseOutTimeInsertReq;
import com.exchange.dto.req.CloseOutTimeUpdateReq;
import com.exchange.enums.FuturesTypeEnum;
import com.exchange.enums.TradingStateEnum;
import com.exchange.exception.BusinessException;
import com.exchange.mapper.CloseOutTimeMapper;
import com.exchange.mapper.FuturesMapper;
import com.exchange.mapper.TradingRecordMapper;
import com.exchange.schedule.CloseOutTimeJobInfo;
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

import java.util.List;

/**
 * create by GYH on 2022/11/27
 */
@Service
@Slf4j
public class CloseOutTimeService implements InitializingBean {
    @Resource
    private CloseOutTimeMapper closeOutTimeMapper;
    @Resource
    private FuturesMapper futuresMapper;
    @Resource
    private TradingRecordMapper tradingRecordMapper;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private MarketService marketService;

    @Override
    public void afterPropertiesSet() {
        List<CloseOutTime> block = closeOutTimeMapper.findAll().collectList().block();
        if (block != null) {
            for (CloseOutTime closeOutTime : block) {
                quartzManager.addJob(new CloseOutTimeJobInfo(closeOutTime.getId().toString(), closeOutTime.getTime(), closeOutTime.getType()));
            }
        }
    }

    public Mono<List<CloseOutTime>> getAll() {
        return closeOutTimeMapper.findAll().collectList();
    }

    /**
     * 强平
     *
     * @param type 类型
     */
    public void closeOut(FuturesTypeEnum type) {
        tradingRecordMapper.findByStateAndFuturesType(TradingStateEnum.BUY, type)
                .flatMap(tradingRecord -> futuresMapper.findById(tradingRecord.getFuturesId())
                        .zipWhen(it -> redisTemplate.opsForValue().get(it.getSymbol()).cast(RedisMarket.class))
                        .flatMap(it -> {
                            Futures futures = it.getT1();
                            RedisMarket redisMarket = it.getT2();
                            return marketService.closeOut(futures, redisMarket, tradingRecord);
                        }))
                .subscribeOn(Schedulers.fromExecutorService(ThreadManager.getQuickPool().getExecutorService())).subscribe();
    }

    /**
     * 修改定时强平时间
     *
     * @param updateReq 请求实体
     * @return 结果
     */
    public Mono<CloseOutTime> updateTime(CloseOutTimeUpdateReq updateReq) {
        CloseOutTime closeOutTime = new CloseOutTime();
        BeanUtils.copyProperties(updateReq, closeOutTime);
        return closeOutTimeMapper.save(closeOutTime)
                .doOnSuccess(it -> quartzManager.modifyJobTime(new CloseOutTimeJobInfo(it.getId().toString(), it.getTime(), it.getType())));
    }

    /**
     * 添加定时强平时间
     *
     * @param insertReq 请求实体
     * @return 结果
     */
    public Mono<CloseOutTime> addCloseOutTime(CloseOutTimeInsertReq insertReq) {
        CloseOutTime closeOutTime = new CloseOutTime();
        BeanUtils.copyProperties(insertReq, closeOutTime);
        closeOutTime.setId(null);
        return closeOutTimeMapper.save(closeOutTime)
                .doOnSuccess(it -> quartzManager.addJob(new CloseOutTimeJobInfo(it.getId().toString(), it.getTime(), it.getType())));
    }

    /**
     * 删除指定的强平时间
     *
     * @param id 强平时间id
     */
    public Mono<Integer> deleteCloseOutTime(Integer id) {
        return closeOutTimeMapper.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("没有该时间id " + id)))
                .flatMap(it -> closeOutTimeMapper.deleteById(id).then(Mono.just(id)))
                .doOnSuccess(it -> {
                    quartzManager.removeJob(new CloseOutTimeJobInfo(id.toString()));
                    log.info("移除强平时间 {}", id);
                });
    }

}

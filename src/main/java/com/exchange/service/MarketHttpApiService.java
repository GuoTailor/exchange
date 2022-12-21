package com.exchange.service;

import com.exchange.clinet.MarketHttpApi;
import com.exchange.clinet.MarketResponse;
import com.exchange.enums.KLinePeriod;
import com.exchange.exception.BusinessException;
import com.exchange.netty.dto.PeriodKLine;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * create by GYH on 2022/12/20
 */
@Slf4j
@Service
public class MarketHttpApiService {
    @Autowired
    private MarketHttpApi marketHttpApi;
    @Autowired
    private ObjectMapper json;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;
    private final String kPrefix = "kline";
    private final ConcurrentHashMap<String, AtomicBoolean> kCache = new ConcurrentHashMap<>();

    /**
     * 获取k线数据
     *
     * @param symbol 合约代码
     * @param period 周期
     * @return 数据
     */
    public Mono<List<PeriodKLine>> getKLine(String symbol, KLinePeriod period) {
        String key = kPrefix + ":" + symbol + ":" + period.getLab();
        return redisTemplate.opsForList().range(key, 0, -1)
                .cast(PeriodKLine.class)
                .collectList()
                .filter(it -> !CollectionUtils.isEmpty(it))
                .switchIfEmpty(Mono.fromCallable(() -> {
                            AtomicBoolean atomicBoolean = kCache.computeIfAbsent(key, it -> new AtomicBoolean(false));
                            synchronized (atomicBoolean) {
                                if (atomicBoolean.compareAndSet(false, true)) {
                                    return getKLine2(symbol, period)
                                            .<List<PeriodKLine>>flatMap(it -> {
                                                atomicBoolean.set(false);
                                                return redisTemplate.opsForList().rightPushAll(key, (Collection) it)
                                                        .<Boolean>flatMap(n -> redisTemplate.expire(key, Duration.ofSeconds(period.getDuration())))
                                                        .<List<PeriodKLine>>map(n -> {
                                                            synchronized (atomicBoolean) {
                                                                atomicBoolean.notifyAll();
                                                            }
                                                            return it;
                                                        });
                                            });
                                } else {
                                    try {
                                        atomicBoolean.wait(60_000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return redisTemplate.opsForList().range(key, 0, -1)
                                            .cast(PeriodKLine.class)
                                            .collectList()
                                            .filter(it -> !CollectionUtils.isEmpty(it));
                                }
                            }
                        })
                        .flatMap(it -> it)
                        .switchIfEmpty(Mono.error(new BusinessException("获取k线失败")))
                        .subscribeOn(Schedulers.boundedElastic()));
    }


    private Mono<List<PeriodKLine>> getKLine2(String symbol, KLinePeriod period) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return marketHttpApi.getKLine(sdf.format(new Date()), period.getLab(), symbol, 0)
                .map(it -> {
                    MarketResponse marketResponse;
                    try {
                        marketResponse = json.readValue(it, MarketResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(e);
                    }
                    String[] split = marketResponse.Obj().split(";");
                    ArrayList<PeriodKLine> ks = new ArrayList<>(split.length);
                    for (String k : split) {
                        PeriodKLine kLine = new PeriodKLine(k);
                        ks.add(kLine);
                    }
                    return ks;
                });
    }

}

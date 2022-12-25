package com.exchange.service;

import com.exchange.clinet.MarketHttpApi;
import com.exchange.clinet.MarketResponse;
import com.exchange.dto.BollResult;
import com.exchange.dto.IndicatorInfo;
import com.exchange.dto.MacdResult;
import com.exchange.enums.KLinePeriod;
import com.exchange.exception.BusinessException;
import com.exchange.dto.PeriodKLine;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @SuppressWarnings("unchecked")
    public Mono<List<IndicatorInfo>> getKLine(String symbol, KLinePeriod period) {
        String key = kPrefix + ":" + symbol + ":" + period.getLab();
        return redisTemplate.opsForList().range(key, 0, -1)
                .cast(IndicatorInfo.class)
                .collectList()
                .filter(it -> !CollectionUtils.isEmpty(it))
                .switchIfEmpty(Mono.fromCallable(() -> {
                            AtomicBoolean atomicBoolean = kCache.computeIfAbsent(key, it -> new AtomicBoolean(false));
                            synchronized (atomicBoolean) {
                                if (atomicBoolean.compareAndSet(false, true)) {
                                    return getIndicator(symbol, period)
                                            .flatMap(it -> {
                                                atomicBoolean.set(false);
                                                return redisTemplate.opsForList().rightPushAll(key, (Collection) it)
                                                        .flatMap(n -> redisTemplate.expire(key, Duration.ofSeconds(period.getDuration())))
                                                        .map(n -> {
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
                                            .cast(IndicatorInfo.class)
                                            .collectList()
                                            .filter(it -> !CollectionUtils.isEmpty(it));
                                }
                            }
                        })
                        .flatMap(it -> it)
                        .switchIfEmpty(Mono.error(new BusinessException("获取k线失败")))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    private Mono<List<IndicatorInfo>> getIndicator(String symbol, KLinePeriod period) {
        return getKLine0(symbol, period)
                .zipWhen(it -> getBoll(it.size(), symbol, period))
                .zipWhen(it -> getMacd(it.getT1().size(), symbol, period))
                .map(it -> {
                    List<PeriodKLine> kline = it.getT1().getT1();
                    Map<Long, BollResult> bollCache = it.getT1().getT2().stream().collect(Collectors.toMap(BollResult::Tick, Function.identity()));
                    Map<Long, MacdResult> macdCache = it.getT2().stream().collect(Collectors.toMap(MacdResult::Tick, Function.identity()));
                    return kline.stream().map(k -> {
                        IndicatorInfo indicatorInfo = new IndicatorInfo();
                        BeanUtils.copyProperties(k, indicatorInfo);
                        indicatorInfo.setTakis(indicatorInfo.getTakis() * 1000);
                        BollResult boll = bollCache.get(k.getTakis());
                        MacdResult macd = macdCache.get(k.getTakis());
                        if (boll != null) {
                            indicatorInfo.setMid(boll.Sma().setScale(2, RoundingMode.HALF_UP));
                            indicatorInfo.setTop(boll.Upper().setScale(2, RoundingMode.HALF_UP));
                            indicatorInfo.setBottom(boll.Lower().setScale(2, RoundingMode.HALF_UP));
                        }
                        if (macd != null) {
                            indicatorInfo.setStick(macd.Histogram().setScale(2, RoundingMode.HALF_UP));
                            indicatorInfo.setDiff(macd.Macd().setScale(2, RoundingMode.HALF_UP));
                            indicatorInfo.setDea(macd.Signal().setScale(2, RoundingMode.HALF_UP));
                        }
                        return indicatorInfo;
                    }).collect(Collectors.toList());
                });
    }

    private Mono<List<PeriodKLine>> getKLine0(String symbol, KLinePeriod period) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return marketHttpApi.getKLine(sdf.format(new Date()), period.getLab(), 1, 500, symbol, 0)
                .map(it -> {
                    MarketResponse marketResponse;
                    try {
                        marketResponse = json.readValue(it, MarketResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(e);
                    }
                    if (!StringUtils.hasText(marketResponse.Obj())) return List.of();
                    String[] split = marketResponse.Obj().split(";");
                    ArrayList<PeriodKLine> ks = new ArrayList<>(split.length);
                    for (String k : split) {
                        PeriodKLine kLine = new PeriodKLine(k);
                        ks.add(kLine);
                    }
                    return ks;
                });
    }

    private Mono<List<BollResult>> getBoll(Integer count, String symbol, KLinePeriod period) {
        return marketHttpApi.getIndicator("{\"lookbackPeriod\":\"20\",\"standardDeviations\":\"2.0\"}", "BOLL", count, period.getLab(), symbol)
                .map(it -> {
                    try {
                        return json.readValue(it, new TypeReference<>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(e);
                    }
                });
    }

    private Mono<List<MacdResult>> getMacd(Integer count, String symbol, KLinePeriod period) {
        return marketHttpApi.getIndicator("{\"fastPeriod\":\"12\",\"slowPeriod\":\"26\",\"signalPeriod\":\"9\"}", "MACD", count, period.getLab(), symbol)
                .map(it -> {
                    try {
                        return json.readValue(it, new TypeReference<>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(e);
                    }
                });
    }

}

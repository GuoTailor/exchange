package com.exchange.service;

import com.exchange.domain.TradingRecord;
import com.exchange.domain.TradingTime;
import com.exchange.domain.User;
import com.exchange.dto.PageInfo;
import com.exchange.dto.RedisMarket;
import com.exchange.dto.req.TradingRecordPageReq;
import com.exchange.dto.req.TradingReq;
import com.exchange.enums.TradingStateEnum;
import com.exchange.enums.TradingTypeEnum;
import com.exchange.exception.BusinessException;
import com.exchange.mapper.TradingRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * create by GYH on 2022/11/22
 */
@Service
public class TradingRecordService {
    private final List<Integer> tradesCountCache = List.of(1, 3, 5, 8, 10);
    @Resource
    private TradingRecordMapper tradingRecordMapper;
    @Autowired
    private FuturesService futuresService;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;
    public static final LocalTime startStopTime = LocalTime.of(16, 30);
    public static final LocalTime endStopTime = LocalTime.of(17, 15);

    /**
     * 买涨
     *
     * @param tradingReq 交易实体
     * @return 交易记录
     */
    public Mono<TradingRecord> tradingRising(TradingReq tradingReq) {
        TradingRecord tradingRecord = new TradingRecord();
        BeanUtils.copyProperties(tradingReq, tradingRecord);
        tradingRecord.setType(TradingTypeEnum.RISING);
        return trading(tradingRecord)
                .filter(it -> it.getS1() != null && it.getS1().compareTo(BigDecimal.ZERO) != 0)
                .switchIfEmpty(Mono.error(new BusinessException("交易失败，涨停")))
                .flatMap(it -> {
                    tradingRecord.setBuyPrice(it.getS1());
                    return tradingRecordMapper.save(tradingRecord);
                });
    }

    /**
     * 买跌
     *
     * @param tradingReq 交易实体
     * @return 交易记录
     */
    public Mono<TradingRecord> tradingFalling(TradingReq tradingReq) {
        TradingRecord tradingRecord = new TradingRecord();
        BeanUtils.copyProperties(tradingReq, tradingRecord);
        tradingRecord.setType(TradingTypeEnum.FALLING);
        return trading(tradingRecord)
                .filter(it -> it.getB1() != null && it.getB1().compareTo(BigDecimal.ZERO) != 0)
                .switchIfEmpty(Mono.error(new BusinessException("交易失败，跌停")))
                .flatMap(it -> {
                    tradingRecord.setBuyPrice(it.getB1());
                    return tradingRecordMapper.save(tradingRecord);
                });
    }

    /**
     * 查询用户的交易记录
     *
     * @param req 条件
     * @return 结果
     */
    public Mono<PageInfo<TradingRecord>> getRecordPage(TradingRecordPageReq req) {
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> tradingRecordMapper.findByUserIdAndBuyTime(it,
                                req.getStartTime(), req.getEndTime(),
                                (req.getPage() - 1) * req.getPageSize(), req.getPageSize())
                        .collectList()
                        .zipWith(tradingRecordMapper.countByUserIdAndBuyTime(it, req.getStartTime(), req.getEndTime()),
                                (data, count) -> PageInfo.ok(count, req, data)));
    }

    /**
     * 获取持仓
     *
     * @return 持仓列表
     */
    public Mono<List<TradingRecord>> getPosition() {
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> tradingRecordMapper.findByUserIdAndState(it, TradingStateEnum.BUY).collectList());
    }

    private Mono<RedisMarket> trading(TradingRecord tradingRecord) {
        tradingRecord.setState(TradingStateEnum.BUY);
        tradingRecord.setBuyTime(LocalDateTime.now());
        BigDecimal tradesCount = BigDecimal.valueOf(tradingRecord.getTradesCount());
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> {
                    tradingRecord.setUserId(it);
                    return futuresService.findByIdAndTime(tradingRecord.getFuturesId());
                })
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("期货商品不存在 " + tradingRecord.getFuturesId())))
                .filter(it -> {
                    boolean b;
                    Calendar instance = Calendar.getInstance();
                    // 判断是否为周末
                    int i = instance.get(Calendar.DAY_OF_WEEK);
                    b = i != Calendar.SATURDAY && i != Calendar.SUNDAY;
                    if (b) {
                        // 判断结算时间
                        LocalTime localTime = tradingRecord.getBuyTime().toLocalTime();
                        b = localTime.isBefore(startStopTime) || localTime.isAfter(endStopTime);
                        // 判断是否是交易时间
                        Iterator<TradingTime> iterator = it.getTradingTime().iterator();
                        while (b && iterator.hasNext()) {
                            TradingTime tradingTime = iterator.next();
                            Duration nowDuration = Duration.ofNanos(localTime.toNanoOfDay());
                            Duration startStopDuration = Duration.ofNanos(startStopTime.toNanoOfDay());
                            // 判断是不是夜盘，如果是夜盘就夜盘收盘前两分钟不能开仓
                            b = tradingTime.getStartTime().compareTo(startStopDuration) > 0
                                    ? nowDuration.compareTo(tradingTime.getStartTime()) > 0 && nowDuration.compareTo(tradingTime.getEndTime().minusMinutes(2L)) < 0
                                    : nowDuration.compareTo(tradingTime.getStartTime()) > 0 && nowDuration.compareTo(tradingTime.getEndTime()) < 0;
                            // 有可能现在是次日
                            if (!b) {
                                nowDuration = nowDuration.plusDays(1);
                                b = tradingTime.getStartTime().compareTo(startStopDuration) > 0
                                        ? nowDuration.compareTo(tradingTime.getStartTime()) > 0 && nowDuration.compareTo(tradingTime.getEndTime().minusMinutes(2L)) < 0
                                        : nowDuration.compareTo(tradingTime.getStartTime()) > 0 && nowDuration.compareTo(tradingTime.getEndTime()) < 0;
                            }
                        }
                    }
                    return b;
                })
                .switchIfEmpty(Mono.error(new BusinessException("连接交易服务器失败1")))
                // 判断止损金额是不是规定的金额
                .filter(it -> tradingRecord.getStopLoss().compareTo(it.getStopLoss().multiply(tradesCount)) != 0)
                .switchIfEmpty(Mono.error(new BusinessException("不支持该止损金额 " + tradingRecord.getStopLoss())))
                // 判断止盈金额是不是规定的金额
                .filter(it -> tradingRecord.getStopProfit().compareTo(it.getStopProfit().multiply(tradesCount)) != 0)
                .switchIfEmpty(Mono.error(new BusinessException("不支持该止盈金额 " + tradingRecord.getStopProfit())))
                // 判断交易次数
                .filter(it -> tradesCountCache.contains(tradingRecord.getTradesCount()))
                .switchIfEmpty(Mono.error(new BusinessException("不支持该交易次数 " + tradingRecord.getTradesCount())))
                .flatMap(it -> {
                    tradingRecord.setDeposit(it.getDeposit().multiply(tradesCount));
                    tradingRecord.setTransactionCost(it.getTransactionCost().multiply(tradesCount));
                    return redisTemplate.opsForValue()
                            .get(it.getSymbol())
                            .filter(Objects::nonNull)
                            .switchIfEmpty(Mono.error(new BusinessException("获取最新交易价格出错 " + it.getSymbol())))
                            .cast(RedisMarket.class)
                            .filter(m -> !m.getTime().plusMinutes(1L).isBefore(tradingRecord.getBuyTime()))
                            .switchIfEmpty(Mono.error(new BusinessException("撮合交易失败")));
                });
    }
}

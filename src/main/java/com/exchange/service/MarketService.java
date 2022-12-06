package com.exchange.service;

import com.exchange.domain.Futures;
import com.exchange.domain.TradingRecord;
import com.exchange.dto.RedisMarket;
import com.exchange.enums.TradingStateEnum;
import com.exchange.enums.TradingTypeEnum;
import com.exchange.mapper.FuturesMapper;
import com.exchange.mapper.TradingRecordMapper;
import com.exchange.netty.NettyClient;
import com.exchange.netty.dto.GeneralMarket;
import com.exchange.util.ThreadManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create by GYH on 2022/11/16
 */
@Service
@Slf4j
public class MarketService implements InitializingBean {
    private final NettyClient nettyClient = new NettyClient();
    @Resource
    private FuturesMapper futuresMapper;
    @Resource
    private TradingRecordMapper tradingRecordMapper;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;
    @Autowired
    private FundAccountService fundAccountService;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Futures> block = futuresMapper.findAll().collectList().block();
        if (block == null) block = List.of();
        String collect = block.stream().map(Futures::getSymbol).collect(Collectors.joining(","));
        nettyClient.setMarkets(collect);
        nettyClient.connect(it -> {
            if ("rm".equals(it.Cmd())) {
                processMarkerMsg(it);
            } else {
                log.info("收到消息 {}", it);
            }
        });
    }

    public Mono<Void> getKLine() {

        return Mono.empty();
    }

    public void processMarkerMsg(GeneralMarket msg) {
        RedisMarket redisMarket = new RedisMarket(msg.M() + msg.S(), msg.P(), msg.B1(), msg.S1(), msg.ZF(), LocalDateTime.now());
        redisTemplate.opsForValue()
                .set(redisMarket.getSymbol(), redisMarket)
                .flatMap(it -> futuresMapper.findBySymbol(redisMarket.getSymbol()))
                .flatMap(futures -> tradingRecordMapper.findByStateAndSymbol(TradingStateEnum.BUY, redisMarket.getSymbol())
                        .flatMap(tradingRecord -> {
                            BigDecimal count = BigDecimal.valueOf(tradingRecord.getTradesCount());
                            //收益 = （最新成交价 - 买入价格） / 最小点位 * 点位金额 * 交易手数
                            BigDecimal price = redisMarket.getP()
                                    .subtract(tradingRecord.getBuyPrice())
                                    .divide(futures.getMiniPoint(), 2, RoundingMode.HALF_DOWN)
                                    .multiply(futures.getPointPrice())
                                    .multiply(count);
                            tradingRecord.setState(TradingStateEnum.STOP_LOSS_SALE);
                            tradingRecord.setSaleTime(LocalDateTime.now());
                            tradingRecord.setSalePrice(redisMarket.getP());
                            //买涨
                            if (tradingRecord.getType() == TradingTypeEnum.RISING) {
                                if (redisMarket.getB1() == null || redisMarket.getB1().compareTo(BigDecimal.ZERO) == 0) {
                                    log.info("平仓 {} 跌停", redisMarket.getSymbol());
                                    return Mono.just("跌停");
                                }
                                // 买涨触发止损 或者触发止盈
                                if (price.negate().compareTo(tradingRecord.getStopLoss()) >= 0 || price.compareTo(tradingRecord.getStopProfit()) >= 0) {
                                    // 损失金额 = 收益 - 交易费用
                                    BigDecimal money = price.subtract(tradingRecord.getTransactionCost());
                                    BigDecimal add = money.add(tradingRecord.getDeposit());
                                    tradingRecord.setEarnings(money);
                                    return tradingRecordMapper.save(tradingRecord)
                                            .filter(t -> add.compareTo(BigDecimal.ZERO) > 0)
                                            .flatMap(t -> fundAccountService.add(tradingRecord.getUserId(), add));
                                } else return Mono.just("买涨未平仓");
                            } else {
                                if (redisMarket.getS1() == null || redisMarket.getS1().compareTo(BigDecimal.ZERO) == 0) {
                                    log.info("平仓 {} 涨停", redisMarket.getSymbol());
                                    return Mono.just("涨停");
                                }
                                //买跌触发止损 或者触发止盈
                                if (price.compareTo(tradingRecord.getStopLoss()) >= 0 || price.negate().compareTo(tradingRecord.getStopProfit()) >= 0) {
                                    // 损失金额 = -(交易费用 + 收益)
                                    BigDecimal money = tradingRecord.getTransactionCost().add(price).negate();
                                    BigDecimal add = money.add(tradingRecord.getDeposit());
                                    tradingRecord.setEarnings(money);
                                    return tradingRecordMapper.save(tradingRecord)
                                            .filter(t -> add.compareTo(BigDecimal.ZERO) > 0)
                                            .flatMap(t -> fundAccountService.add(tradingRecord.getUserId(), add));
                                } else return Mono.just("买跌未平仓");
                            }
                        })
                        .then())
                .subscribeOn(Schedulers.fromExecutorService(ThreadManager.getQuickPool().getExecutorService())).subscribe();
    }

    /**
     * 强平
     *
     * @param futures       期货
     * @param redisMarket   实时行情
     * @param tradingRecord 交易记录
     */
    public Mono<?> closeOut(Futures futures, RedisMarket redisMarket, TradingRecord tradingRecord) {
        BigDecimal count = BigDecimal.valueOf(tradingRecord.getTradesCount());
        //收益 = （最新成交价 - 买入价格） / 最小点位 * 点位金额 * 交易手数
        BigDecimal price = redisMarket.getP()
                .subtract(tradingRecord.getBuyPrice())
                .divide(futures.getMiniPoint(), 2, RoundingMode.HALF_DOWN)
                .multiply(futures.getPointPrice())
                .multiply(count);
        tradingRecord.setState(TradingStateEnum.FORCED_LIQUIDATION_SALE);
        tradingRecord.setSaleTime(LocalDateTime.now());
        tradingRecord.setSalePrice(redisMarket.getP());
        BigDecimal money;
        //买涨
        if (tradingRecord.getType() == TradingTypeEnum.RISING) {
            if (redisMarket.getB1() == null || redisMarket.getB1().compareTo(BigDecimal.ZERO) == 0) {
                log.info("强平 {} 跌停", redisMarket.getSymbol());
                return Mono.just("跌停");
            }
            // 损失金额 = 收益 - 交易费用
            money = price.subtract(tradingRecord.getTransactionCost());
        } else {
            if (redisMarket.getS1() == null || redisMarket.getS1().compareTo(BigDecimal.ZERO) == 0) {
                log.info("强平 {} 涨停", redisMarket.getSymbol());
                return Mono.just("涨停");
            }
            // 损失金额 = -(交易费用 + 收益)
            money = tradingRecord.getTransactionCost().add(price).negate();
        }
        BigDecimal add = money.add(tradingRecord.getDeposit());
        tradingRecord.setEarnings(money);
        return tradingRecordMapper.save(tradingRecord)
                .filter(t -> add.compareTo(BigDecimal.ZERO) > 0)
                .flatMap(t -> fundAccountService.add(tradingRecord.getUserId(), add));
    }

}

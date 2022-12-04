package com.exchange.mapper;

import com.exchange.domain.TradingRecord;
import com.exchange.enums.FuturesTypeEnum;
import com.exchange.enums.TradingStateEnum;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * create by GYH on 2022/11/22
 */
public interface TradingRecordMapper extends R2dbcRepository<TradingRecord, Integer> {

    @Query("select tr.* from trading_record tr, futures f " +
            " where tr.state = :state and f.symbol = :symbol and f.id = tr.futures_id")
    Flux<TradingRecord> findByStateAndSymbol(TradingStateEnum state, String symbol);

    @Query("select tr.* from trading_record tr, futures f " +
            " where tr.state = :state and f.type = :type and f.id = tr.futures_id")
    Flux<TradingRecord> findByStateAndFuturesType(TradingStateEnum state, FuturesTypeEnum type);

    @Query("select tr.* from trading_record tr " +
            " where tr.state = :state and tr.futures_id = :futuresId ")
    Flux<TradingRecord> findByStateAndFuturesId(TradingStateEnum state, Integer futuresId);

    @Query("select * from trading_record" +
            " where user_id = :userId and buy_time between :startTime and :endTime" +
            " order by buy_time desc" +
            " limit :limit offset :offset ")
    Flux<TradingRecord> findByUserIdAndBuyTime(Integer userId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime,
                                               Integer offset,
                                               Integer limit);

    @Query("select count(*) from trading_record where user_id = :userId and buy_time between :startTime and :endTime")
    Mono<Long> countByUserIdAndBuyTime(Integer userId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("select * from trading_record where user_id = :userId and state = :state")
    Flux<TradingRecord> findByUserIdAndState(Integer userId, TradingStateEnum state);
}

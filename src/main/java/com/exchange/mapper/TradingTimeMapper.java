package com.exchange.mapper;

import com.exchange.domain.TradingTime;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

/**
 * create by GYH on 2022/11/27
 */
public interface TradingTimeMapper extends R2dbcRepository<TradingTime, Integer> {

    @Query("select * from trading_time where futures_id = :futuresId")
    Flux<TradingTime> findByFuturesId(Integer futuresId);
}

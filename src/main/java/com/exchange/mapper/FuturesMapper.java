package com.exchange.mapper;

import com.exchange.domain.Futures;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/11/16
 */
public interface FuturesMapper extends R2dbcRepository<Futures, Integer> {

    @Query("select * from futures where symbol = :symbol")
    Mono<Futures> findBySymbol(String symbol);
}

package com.exchange.mapper;

import com.exchange.domain.RealName;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/11/19
 */
public interface RealNameMapper extends R2dbcRepository<RealName, Integer> {
    @Query("select * from real_name where user_id = :userId and state = 'PAAS' limit 1")
    Mono<RealName> isRealName(Integer userId);
}

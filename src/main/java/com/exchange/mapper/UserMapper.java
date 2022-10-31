package com.exchange.mapper;

import com.exchange.domain.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/10/28
 */
public interface UserMapper extends R2dbcRepository<User, Integer> {

    @Query("select * from user where username = :username")
    Mono<User> findByUsername(String username);
}

package com.exchange.mapper;

import com.exchange.domain.RealName;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * create by GYH on 2022/11/19
 */
public interface RealNameMapper extends R2dbcRepository<RealName, Integer> {
}

package com.exchange.mapper;

import com.exchange.domain.ParameterLog;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * create by GYH on 2022/9/22
 */
public interface ParameterLogMapper extends R2dbcRepository<ParameterLog, Integer> {

}
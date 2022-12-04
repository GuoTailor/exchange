package com.exchange.mapper;

import com.exchange.domain.CloseOutTime;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * create by GYH on 2022/11/27
 */
public interface CloseOutTimeMapper extends R2dbcRepository<CloseOutTime, Integer> {
}

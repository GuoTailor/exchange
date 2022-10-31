package com.exchange.mapper;

import com.exchange.domain.Role;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * create by GYH on 2022/10/28
 */
public interface RoleMapper extends R2dbcRepository<Role, Integer> {
}

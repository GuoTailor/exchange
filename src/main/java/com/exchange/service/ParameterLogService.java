package com.exchange.service;

import com.exchange.domain.ParameterLog;
import com.exchange.mapper.ParameterLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ParameterLogService {
    @Resource
    private ParameterLogMapper parameterLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public Mono<ParameterLog> create() {
        var log = new ParameterLog(null, "/nmka", null, null, null, null, LocalDateTime.now(), 12, null, null);
        return parameterLogMapper.save(log)
                .map(it -> {
                    System.out.println(it);
                    if (it.url().equals("/nmka")) {
                        throw new IllegalArgumentException("fail");
                    }
                    return it;
                });
    }
}

package com.exchange;

import com.exchange.domain.ParameterLog;
import com.exchange.mapper.ParameterLogMapper;
import com.exchange.service.ParameterLogService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExchangeApplicationTests {
    @Resource
    private ParameterLogMapper parameterLogMapper;
    @Autowired
    private ParameterLogService logService;

    @Test
    public void testTransactional() {
        ParameterLog block = logService.create()
                .map(it -> {
                    System.out.println(it);
                    return it;
                })
                .doOnError(it -> System.out.println(it.getMessage() + " >>>> "))
                .block();
        System.out.println(block);
    }

    @Test
    void contextLoads() {
        ParameterLog block = parameterLogMapper.findById(1023868930).block();
        System.out.println(block);
    }

}

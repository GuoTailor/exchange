package com.exchange;

import com.exchange.domain.RealName;
import com.exchange.domain.TradingTime;
import com.exchange.dto.RedisMarket;
import com.exchange.dto.req.RealNameReq;
import com.exchange.mapper.RealNameMapper;
import com.exchange.mapper.TradingTimeMapper;
import com.exchange.service.CloseOutTimeService;
import com.exchange.service.RealNameService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
class ExchangeApplicationTests {
    @Resource
    private RealNameMapper realNameMapper;
    @Resource
    private TradingTimeMapper tradingTimeMapper;
    @Autowired
    private RealNameService realNameService;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CloseOutTimeService closeOutTimeService;

    @Test
    public void testReal() {
        RealNameReq realName = new RealNameReq();
        realNameService.addRecord(realName).block();
    }

    @Test
    public void testEnum() {
        RealName realName = new RealName();
        realName.setId(1);
        realName.setRemark("nmka");
        // 无法增量更新
        realNameMapper.save(realName).block();
    }

    @Test
    public void testGetEnum() {
        RealName block = realNameMapper.findById(1).block();
        System.out.println(block);
    }

    @Test
    public void testRedis() {
        RedisMarket block = redisTemplate.opsForValue()
                .set("test", new RedisMarket("helo", BigDecimal.valueOf(12.3), BigDecimal.valueOf(13.2), BigDecimal.valueOf(0.89), BigDecimal.valueOf(0.89), LocalDateTime.now()))
                .flatMap(it -> redisTemplate.opsForValue().get("test"))
                .cast(RedisMarket.class)
                .block();
        System.out.println(block);
    }

    @Test
    public void testBean() {
        TradingTime block = tradingTimeMapper.findById(1).block();
        System.out.println(block);
    }

    @Test
    public void testDelete() {
        Integer block = closeOutTimeService.deleteCloseOutTime(1).block();
        System.out.println(block);
    }

}

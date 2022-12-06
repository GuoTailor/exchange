package com.exchange.dto;

import com.exchange.config.LocalDateTimeDeserializer;
import com.exchange.config.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/11/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisMarket {
    private String symbol;
    //当前价
    private BigDecimal P;
    //买一
    private BigDecimal B1;
    //卖一
    private BigDecimal S1;
    //单日涨幅
    private BigDecimal ZF;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime time;
}

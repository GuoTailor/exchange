package com.exchange.netty.dto;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * create by GYH on 2022/11/23
 */
@Data
public class PeriodKLine {
    /**
     * 时间 精确到秒
     */
    private LocalDateTime takis;
    /**
     * 开
     */
    private BigDecimal O;
    /**
     * 高
     */
    private BigDecimal H;
    /**
     * 低
     */
    private BigDecimal L;
    /**
     * 量
     */
    private Long V;

    public PeriodKLine(String data) {
        if (StringUtils.hasText(data)) {
            String[] split = data.split(",");
            takis = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(split[0]) * 100), ZoneId.systemDefault());
            O = new BigDecimal(split[1]);
            H = new BigDecimal(split[2]);
            L = new BigDecimal(split[3]);
            V = Long.parseLong(split[4]);
        }
    }
}

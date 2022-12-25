package com.exchange.dto;

import java.math.BigDecimal;

/**
 * create by GYH on 2022/12/25
 */
public record BollResult(Long Tick,
                         BigDecimal Sma,
                         BigDecimal Upper,
                         BigDecimal Lower,
                         BigDecimal PercentB,
                         BigDecimal ZScore,
                         BigDecimal Width) {
}

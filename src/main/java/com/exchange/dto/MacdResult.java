package com.exchange.dto;

import java.math.BigDecimal;

/**
 * create by GYH on 2022/12/25
 */
public record MacdResult(Long Tick,
                         BigDecimal Histogram,
                         BigDecimal Macd,
                         BigDecimal Signal
) {
}

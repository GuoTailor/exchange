package com.exchange.dto;

import com.exchange.domain.Futures;
import com.exchange.domain.TradingTime;
import com.exchange.enums.FuturesTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "期货和时间")
public class FuturesAndTime extends Futures {

    @Schema(description = "交易时间")
    private List<TradingTime> tradingTime;
}


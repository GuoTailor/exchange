package com.exchange.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易
 */
@Data
public class TradingReq {


    /**
     * 期货id
     */
    @Schema(description = "期货id")
    @NotNull
    private Integer futuresId;

    /**
     * 交易次数
     */
    @Schema(description = "交易次数")
    @NotNull
    private Integer tradesCount;

    /**
     * 止损金额
     */
    @Schema(description = "止损金额")
    @NotNull
    private BigDecimal stopLoss;

    /**
     * 止盈金额
     */
    @Schema(description = "止盈金额")
    @NotNull
    private BigDecimal stopProfit;


}


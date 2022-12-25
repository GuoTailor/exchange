package com.exchange.domain;

import com.exchange.enums.FuturesTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Schema(description = "期货")
public class Futures {
    @Id
    private Integer id;

    /**
     * 商品名字
     */
    @Schema(description = "商品名字")
    private String name;

    /**
     * 合约代码
     */
    @Schema(description = "合约代码")
    private String symbol;

    /**
     * 最低价格
     */
    @Schema(description = "最低价格")
    private BigDecimal minimumPrice;

    /**
     * 最高价格
     */
    @Schema(description = "最高价格")
    private BigDecimal topPrice;

    /**
     * 是否显示为自选
     */
    @Schema(description = "是否显示为自选")
    private Boolean optional;

    /**
     * 所在组名
     */
    @Schema(description = "所在组名")
    private String group;

    /**
     * 最新价
     */
    @Schema(description = "最新价")
    private BigDecimal latestPrice;

    /**
     * 止损金额
     */
    @Schema(description = "止损金额")
    private BigDecimal stopLoss;

    /**
     * 止盈金额
     */
    @Schema(description = "止盈金额")
    private BigDecimal stopProfit;

    /**
     * 交易费用
     */
    @Schema(description = "交易费用")
    private BigDecimal transactionCost;
    /**
     * 保证金
     */
    @Schema(description = "保证金")
    private BigDecimal deposit;
    /**
     * 最小点位
     */
    @Schema(description = "最小点位")
    private BigDecimal miniPoint;
    /**
     * 点位金额
     */
    @Schema(description = "点位金额")
    private BigDecimal pointPrice;

    @Schema(description = "类型")
    private FuturesTypeEnum type;

    @Schema(description = "简称")
    private String abbreviation;

    @Schema(description = "备注")
    private String intro;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "是否启用 true：启用")
    private Boolean enabled;
}


package com.exchange.domain;

import com.exchange.enums.TradingStateEnum;
import com.exchange.enums.TradingTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易
 */
@Data
@Accessors(chain = true)
public class TradingRecord {

    @Id
    private Integer id;

    /**
     * 用户id
     */
    @Schema(description = "用户id" )
    private Integer userId;

    /**
     * 期货id
     */
    @Schema(description = "期货id" )
    private Integer futuresId;

    /**
     * 交易次数
     */
    @Schema(description = "交易次数" )
    private Integer tradesCount;

    /**
     * 止损金额
     */
    @Schema(description = "止损金额" )
    private BigDecimal stopLoss;

    /**
     * 止盈金额
     */
    @Schema(description = "止盈金额" )
    private BigDecimal stopProfit;

    /**
     * 交易费用
     */
    @Schema(description = "交易费用" )
    private BigDecimal transactionCost;

    /**
     * 保证金
     */
    @Schema(description = "保证金" )
    private BigDecimal deposit;

    /**
     * 买入价格
     */
    @Schema(description = "买入价格" )
    private BigDecimal buyPrice;

    /**
     * 卖出价格
     */
    @Schema(description = "卖出价格" )
    private BigDecimal salePrice;

    /**
     * 状态TradingStateEnum
     */
    @Schema(description = "状态TradingStateEnum" )
    private TradingStateEnum state;

    /**
     * 买涨买跌
     */
    @Schema(description = "买涨买跌" )
    private TradingTypeEnum type;

    /**
     * 买入时间
     */
    @Schema(description = "买入时间" )
    private LocalDateTime buyTime;

    /**
     * 卖出时间
     */
    @Schema(description = "卖出时间" )
    private LocalDateTime saleTime;

    /**
     * 收益
     */
    @Schema(description = "收益" )
    private BigDecimal earnings;

    @Version
    private Integer version;

    /**
     * true删除
     */
    @Schema(description = "true删除" )
    private Boolean deleteFlag;

}


package com.exchange.domain;

import com.exchange.enums.TradingStateEnum;
import com.exchange.enums.TradingTypeEnum;
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
    private Integer userId;

    /**
     * 期货id
     */
    private Integer futuresId;

    /**
     * 交易次数
     */
    private Integer tradesCount;

    /**
     * 止损金额
     */
    private BigDecimal stopLoss;

    /**
     * 止盈金额
     */
    private BigDecimal stopProfit;

    /**
     * 交易费用
     */
    private BigDecimal transactionCost;

    /**
     * 保证金
     */
    private BigDecimal deposit;

    /**
     * 买入价格
     */
    private BigDecimal buyPrice;

    /**
     * 卖出价格
     */
    private BigDecimal salePrice;

    /**
     * 状态TradingStateEnum
     */
    private TradingStateEnum state;

    /**
     * 买涨买跌
     */
    private TradingTypeEnum type;

    /**
     * 买入时间
     */
    private LocalDateTime buyTime;

    /**
     * 卖出时间
     */
    private LocalDateTime saleTime;

    /**
     * 收益
     */
    private BigDecimal earnings;

    @Version
    private Integer version;

    /**
     * true删除
     */
    private Boolean deleteFlag;

}


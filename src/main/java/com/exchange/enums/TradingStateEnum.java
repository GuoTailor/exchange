package com.exchange.enums;

/**
 * 交易记录状态
 * create by GYH on 2022/11/20
 */
public enum TradingStateEnum {
    BUY,
    /**
     * 止损卖
     */
    STOP_LOSS_SALE,
    /**
     * 强平卖
     */
    FORCED_LIQUIDATION_SALE;
}

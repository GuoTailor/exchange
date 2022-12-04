package com.exchange.netty;

import com.exchange.netty.dto.GeneralMarket;

/**
 * create by GYH on 2022/10/11
 */
public interface MessageListener {
    void onMessage(GeneralMarket request) throws InterruptedException;
}

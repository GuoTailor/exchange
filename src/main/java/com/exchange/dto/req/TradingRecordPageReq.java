package com.exchange.dto.req;

import com.exchange.dto.PageReq;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * create by GYH on 2022/12/2
 */
@Value
public class TradingRecordPageReq extends PageReq {
    /**
     * 开始时间
     */
    LocalDateTime startTime;

    /**
     * 结束时间
     */
    LocalDateTime endTime;
}

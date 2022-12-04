package com.exchange.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Duration;

/**
 * 交易时间
 */
@Data
@Schema(description = "交易时间")
public class TradingTime {
    @Id
    private Integer id;

    /**
     * 期货id
     */
    @Schema(description = "期货id")
    private Integer futuresId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Duration startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Duration endTime;

}


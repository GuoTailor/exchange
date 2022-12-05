package com.exchange.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;

/**
 * create by GYH on 2022/12/4
 */
@Data
@Schema(description = "交易时间请求")
public class TradingTimeUpdateReq {

    @NotNull
    private Integer id;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间,传秒，如 10：30：00 就传 10*60*60 + 30*60的秒数")
    @NotNull
    private Duration startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间,传秒，如 10：30：00 就传 10*60*60 + 30*60的秒数")
    @NotNull
    private Duration endTime;
}

package com.exchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * create by GYH on 2022/12/25
 */
@Data
@Schema(description = "k线")
public class IndicatorInfo {
    /**
     * 时间 毫秒
     */
    @Schema(description = "时间 毫秒")
    private Long takis;
    /**
     * 收
     */
    @Schema(description = "收")
    private String c;
    /**
     * 开
     */
    @Schema(description = "开")
    private String o;
    /**
     * 高
     */
    @Schema(description = "高")
    private String h;
    /**
     * 低
     */
    @Schema(description = "低")
    private String l;
    /**
     * 额
     */
    @Schema(description = "额")
    private String a;
    /**
     * 量
     */
    @Schema(description = "量")
    private String v;

    private BigDecimal diff;
    private BigDecimal dea;
    private BigDecimal stick;
    private BigDecimal mid;
    private BigDecimal top;
    private BigDecimal bottom;
}

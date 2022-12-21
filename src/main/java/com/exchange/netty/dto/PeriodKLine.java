package com.exchange.netty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * create by GYH on 2022/11/23
 */
@Data
@Schema(description = "k线")
public class PeriodKLine {
    /**
     * 时间 毫秒
     */
    @Schema(description = "时间 毫秒")
    private String takis;
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

    public PeriodKLine(String data) {
        if (StringUtils.hasText(data)) {
            String[] split = data.split(",");
            takis = split[0] + "000";
            c = split[1];
            o = split[2];
            h = split[3];
            l = split[4];
            a = split[5];
            v = split[6];
        }
    }

    public PeriodKLine() {
    }
}

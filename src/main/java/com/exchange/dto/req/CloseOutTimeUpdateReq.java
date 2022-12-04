package com.exchange.dto.req;

import com.exchange.enums.FuturesTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;

@Data
@Schema(description = "更新国内外产品的强平时间")
public class CloseOutTimeUpdateReq {

    @NotNull
    @Schema(description = "id")
    private Integer id;

    /**
     * 强平时间
     */
    @NotNull
    @Schema(description = "强平时间,传秒，如 10：30：00 就传 10*60*60 + 30*60的秒数")
    private Duration time;

    /**
     * 国内外类型
     */
    @NotNull
    @Schema(description = "国内外类型")
    private FuturesTypeEnum type;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}


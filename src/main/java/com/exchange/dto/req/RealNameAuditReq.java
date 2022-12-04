package com.exchange.dto.req;

import com.exchange.enums.RealNameAuthEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "实名")
public class RealNameAuditReq {

    @Schema(description = "实名认证id")
    @NotNull
    private Integer id;

    /**
     * 状态RealNameAuthEnum
     */
    @Schema(description = "状态RealNameAuthEnum")
    @NotNull(message = "状态不能为空")
    private RealNameAuthEnum state;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}


package com.exchange.dto.req;

import com.exchange.enums.AuditStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核信息
 * create by GYH on 2022/11/21
 */
@Data
@Schema(description = "审核信息")
public class AuditInfoReq {
    @Schema(description = "审核记录的id")
    @NotNull
    private Integer id;

    @Schema(description = "审核状态")
    @NotNull
    private AuditStatus state;

    @Schema(description = "审核拒绝时的原因")
    private String remark;
}

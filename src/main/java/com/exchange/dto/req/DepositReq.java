package com.exchange.dto.req;

import com.exchange.enums.DepositTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * create by GYH on 2022/11/20
 */
@Data
@Schema(description = "充值实体")
public class DepositReq {
    @Schema(description = "金额")
    @NotNull
    private BigDecimal money;

    @NotNull
    @Schema(description = "充值类型")
    private DepositTypeEnum depositType;
}

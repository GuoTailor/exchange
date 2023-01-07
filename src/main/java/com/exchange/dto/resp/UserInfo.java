package com.exchange.dto.resp;

import com.exchange.enums.RealNameAuthEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * create by GYH on 2022/12/5
 */
@Data
@Schema(description = "用户信息")
public class UserInfo {
    @Schema(description = "账户编号")
    private Integer id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户头像")
    private String head;
    @Schema(description = "余额")
    private BigDecimal balance;
    @Schema(description = "用户状态")
    private Boolean enable;
    @Schema(description = "是否实名")
    private Boolean realName;
    @Schema(description = "实名状态")
    private RealNameAuthEnum state;
    @Schema(description = "注册时间")
    private LocalDateTime createTime;
}

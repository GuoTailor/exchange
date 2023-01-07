package com.exchange.dto.resp;

import com.exchange.enums.InvitationStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Schema(description = "邀请码实体")
public class InvitationInfo {
    /**
     *
     */
    @Schema(description = "")
    private Integer id;

    /**
     * 邀请码
     */
    @Schema(description = "邀请码")
    private String invitationCode;

    /**
     * 业务员id
     */
    @Schema(description = "业务员id")
    private Integer businessId;

    @Schema(description = "使用状态")
    private InvitationStateEnum state;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Integer userId;

    /**
     * 使用时间
     */
    @Schema(description = "使用时间")
    private LocalDateTime useTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "使用账户")
    private String account;

    @Schema(description = "创建人")
    private String creator;
}

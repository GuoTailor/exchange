package com.exchange.dto.req;

import com.exchange.dto.PageReq;
import com.exchange.enums.InvitationStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * create by GYH on 2023/1/7
 */
@Data
@Schema(description = "邀请码请求")
public class InvitationPageReq extends PageReq {
    @Schema(description = "使用状态")
    private InvitationStateEnum useState;
    @Schema(description = "关键词")
    private String keyword;
}

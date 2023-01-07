package com.exchange.dto.req;

import com.exchange.dto.PageReq;
import com.exchange.enums.RealNameAuthEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

/**
 * create by GYH on 2022/12/28
 */
@Value
@Schema(description = "用户信息查询实体")
public class UserInfoPageReq extends PageReq {

    /**
     * 用户状态
     */
    @Schema(description = "用户状态")
    Boolean enable;

    /**
     * 状态RealNameAuthEnum
     */
    @Schema(description = "实名审核状态RealNameAuthEnum")
    RealNameAuthEnum state;

    @Schema(description = "关键词 姓名、登录名")
    String keywords;
}

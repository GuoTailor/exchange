package com.exchange.domain;

import com.exchange.enums.RealNameAuthEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Schema(description = "实名")
public class RealName {

    @Id
    private Integer id;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Integer userId;

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    private String idNumber;

    /**
     * 开户行
     */
    @Schema(description = "开户行")
    private String openingBank;

    /**
     * 银行卡号
     */
    @Schema(description = "银行卡号")
    private String cardNo;

    /**
     * 身份证正面
     */
    @Schema(description = "身份证正面")
    private String identityFrontUrl;

    /**
     * 身份证反面
     */
    @Schema(description = "身份证反面")
    private String identityReverseUrl;

    /**
     * 银行卡正面
     */
    @Schema(description = "银行卡正面")
    private String bankFrontUrl;

    /**
     * 状态RealNameAuthEnum
     */
    @Schema(description = "状态RealNameAuthEnum")
    private RealNameAuthEnum state;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}


package com.exchange.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "实名")
public class RealNameReq {

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    @NotBlank
    private String idNumber;

    /**
     * 开户行
     */
    @Schema(description = "开户行")
    @NotBlank
    private String openingBank;

    /**
     * 银行卡号
     */
    @Schema(description = "银行卡号")
    @NotBlank
    private String cardNo;

    /**
     * 身份证正面
     */
    @Schema(description = "身份证正面")
    @NotBlank
    private String identityFrontUrl;

    /**
     * 身份证反面
     */
    @Schema(description = "身份证反面")
    @NotBlank
    private String identityReverseUrl;

    /**
     * 银行卡正面
     */
    @Schema(description = "银行卡正面")
    @NotBlank
    private String bankFrontUrl;

}


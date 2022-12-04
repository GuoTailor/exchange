package com.exchange.controller;

import com.exchange.domain.FundRecord;
import com.exchange.dto.PageInfo;
import com.exchange.dto.ResponseInfo;
import com.exchange.dto.req.AuditInfoReq;
import com.exchange.dto.req.DepositReq;
import com.exchange.dto.req.FundRecordPageReq;
import com.exchange.service.FundAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * create by GYH on 2022/11/22
 */
@Tag(name = "资金账户")
@RestController
@RequestMapping("/fund/account")
public class FundAccountController {
    @Autowired
    private FundAccountService fundAccountService;

    /**
     * 充值
     *
     * @param req 充值实体
     */
    @Operation(summary = "充值", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/deposit")
    public Mono<ResponseInfo<FundRecord>> deposit(@RequestBody @Valid DepositReq req) {
        return fundAccountService.deposit(req).map(ResponseInfo::ok);
    }

    /**
     * 提现
     *
     * @param money 提现金额
     */
    @Operation(summary = "提现", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/withdraw")
    public Mono<ResponseInfo<FundRecord>> withdraw(@RequestParam BigDecimal money) {
        return fundAccountService.withdraw(money).map(ResponseInfo::ok);
    }

    /**
     * 审核
     *
     * @param auditInfoReq 请求实体
     */
    @Operation(summary = "审核", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/audit")
    public Mono<ResponseInfo<FundRecord>> audit(@RequestBody @Valid AuditInfoReq auditInfoReq) {
        return fundAccountService.audit(auditInfoReq).map(ResponseInfo::ok);
    }

    /**
     * 分页查询提现充值记录
     *
     * @param pageReq 分页实体
     * @return 充值提现记录
     */
    @Operation(summary = "分页查询提现充值记录", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/page")
    public Mono<PageInfo<FundRecord>> selectPage(@RequestBody FundRecordPageReq pageReq) {
        return fundAccountService.selectPage(pageReq);
    }
}

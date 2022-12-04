package com.exchange.controller;

import com.exchange.domain.RealName;
import com.exchange.dto.ResponseInfo;
import com.exchange.dto.req.RealNameAuditReq;
import com.exchange.dto.req.RealNameReq;
import com.exchange.service.RealNameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/11/22
 */
@Tag(name = "实名")
@RestController
@RequestMapping("/real/name")
public class RealNameController {
    @Autowired
    private RealNameService realNameService;

    @Operation(summary = "添加实名", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public Mono<ResponseInfo<RealName>> addRecord(@RequestBody @Valid RealNameReq realName) {
        return realNameService.addRecord(realName).map(ResponseInfo::ok);
    }

    @Operation(summary = "审核", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/audit")
    public Mono<ResponseInfo<RealName>> audit(@RequestBody @Valid RealNameAuditReq realNameAuditReq) {
        return realNameService.audit(realNameAuditReq).map(ResponseInfo::ok);
    }
}

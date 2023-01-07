package com.exchange.controller;

import com.exchange.domain.Invitation;
import com.exchange.dto.PageInfo;
import com.exchange.dto.ResponseInfo;
import com.exchange.dto.req.InvitationPageReq;
import com.exchange.dto.resp.InvitationInfo;
import com.exchange.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2023/1/7
 */
@Tag(name = "邀请码")
@Slf4j
@RestController
@RequestMapping("/invitation")
public class InvitationController {
    @Autowired
    private InvitationService invitationService;

    @Operation(summary = "生成邀请码", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('BUSINESS')")
    @GetMapping("/create")
    public Mono<ResponseInfo<Invitation>> create() {
        return invitationService.createInvitation().map(ResponseInfo::ok);
    }

    @Operation(summary = "获取邀请码列表", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('BUSINESS')")
    @PostMapping("/page")
    public Mono<PageInfo<InvitationInfo>> pageList(@RequestBody InvitationPageReq req) {
        return invitationService.pageList(req);
    }
}

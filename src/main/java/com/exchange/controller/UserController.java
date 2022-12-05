package com.exchange.controller;

import com.exchange.dto.ResponseInfo;
import com.exchange.dto.resp.UserInfo;
import com.exchange.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/12/5
 */
@Tag(name = "用户")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/info")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取用户信息", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<UserInfo>> getUserInfo() {
        return userService.getUserInfo().map(ResponseInfo::ok);
    }

    @PostMapping("/password/update")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "更新密码", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<?>> updatePassword() {
        return Mono.just("更新nm").map(ResponseInfo::ok);
    }

    @PostMapping("/password/forget")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "忘记密码", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<?>> forgetPassword() {
        return Mono.just("忘记nm").map(ResponseInfo::ok);
    }
}

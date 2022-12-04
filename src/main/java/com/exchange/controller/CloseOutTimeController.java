package com.exchange.controller;

import com.exchange.domain.CloseOutTime;
import com.exchange.dto.ResponseInfo;
import com.exchange.dto.req.CloseOutTimeInsertReq;
import com.exchange.dto.req.CloseOutTimeUpdateReq;
import com.exchange.service.CloseOutTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * create by GYH on 2022/12/1
 */
@Tag(name = "强平时间")
@RestController
@RequestMapping("/close/out/time")
public class CloseOutTimeController {
    @Autowired
    private CloseOutTimeService closeOutTimeService;

    @Operation(summary = "获取所有定时强平时间", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Mono<ResponseInfo<List<CloseOutTime>>> getAll() {
        return closeOutTimeService.getAll().map(ResponseInfo::ok);
    }

    /**
     * 修改定时强平时间
     *
     * @param updateReq 请求实体
     * @return 结果
     */
    @Operation(summary = "修改定时强平时间", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/update")
    public Mono<ResponseInfo<CloseOutTime>> updateTime(@RequestBody @Valid CloseOutTimeUpdateReq updateReq) {
        return closeOutTimeService.updateTime(updateReq).map(ResponseInfo::ok);
    }

    /**
     * 添加定时强平时间
     *
     * @param insertReq 请求实体
     * @return 结果
     */
    @Operation(summary = "添加定时强平时间", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add")
    public Mono<ResponseInfo<CloseOutTime>> addCloseOutTime(@RequestBody @Valid CloseOutTimeInsertReq insertReq) {
        return closeOutTimeService.addCloseOutTime(insertReq).map(ResponseInfo::ok);
    }

    /**
     * 删除指定的强平时间
     *
     * @param id 强平时间id
     */
    @Operation(summary = "删除指定的强平时间", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/delete")
    public Mono<ResponseInfo<Integer>> deleteCloseOutTime(@RequestParam Integer id) {
        return closeOutTimeService.deleteCloseOutTime(id).map(ResponseInfo::ok);
    }
}

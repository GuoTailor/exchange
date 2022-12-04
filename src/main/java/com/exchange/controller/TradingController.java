package com.exchange.controller;

import com.exchange.domain.TradingRecord;
import com.exchange.dto.PageInfo;
import com.exchange.dto.ResponseInfo;
import com.exchange.dto.req.TradingRecordPageReq;
import com.exchange.dto.req.TradingReq;
import com.exchange.service.TradingRecordService;
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

import java.util.List;

/**
 * create by GYH on 2022/12/2
 */
@Tag(name = "交易相关")
@RestController
@RequestMapping("/trading")
public class TradingController {
    @Autowired
    private TradingRecordService tradingRecordService;

    /**
     * 买涨
     *
     * @param tradingReq 交易实体
     * @return 交易记录
     */
    @Operation(summary = "买涨", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rising")
    public Mono<ResponseInfo<TradingRecord>> tradingRising(@RequestBody @Valid TradingReq tradingReq) {
        return tradingRecordService.tradingRising(tradingReq).map(ResponseInfo::ok);
    }

    /**
     * 买跌
     *
     * @param tradingReq 交易实体
     * @return 交易记录
     */
    @Operation(summary = "买跌", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/falling")
    public Mono<ResponseInfo<TradingRecord>> tradingFalling(@RequestBody @Valid TradingReq tradingReq) {
        return tradingRecordService.tradingFalling(tradingReq).map(ResponseInfo::ok);
    }

    /**
     * 查询用户的交易记录
     *
     * @param req 条件
     * @return 结果
     */
    @Operation(summary = "查询用户的交易记录", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/record")
    public Mono<PageInfo<TradingRecord>> getRecordPage(@RequestBody TradingRecordPageReq req) {
        return tradingRecordService.getRecordPage(req);
    }

    /**
     * 获取持仓
     *
     * @return 持仓列表
     */
    @Operation(summary = "获取持仓", security = {@SecurityRequirement(name = "Authorization")})
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/position")
    public Mono<ResponseInfo<List<TradingRecord>>> getPosition() {
        return tradingRecordService.getPosition().map(ResponseInfo::ok);
    }

}

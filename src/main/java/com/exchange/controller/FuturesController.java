package com.exchange.controller;

import com.exchange.domain.Futures;
import com.exchange.domain.TradingTime;
import com.exchange.dto.FuturesAndTime;
import com.exchange.dto.ResponseInfo;
import com.exchange.dto.req.TradingTimeInstallReq;
import com.exchange.dto.req.TradingTimeUpdateReq;
import com.exchange.service.FuturesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

/**
 * create by GYH on 2022/11/16
 */
@Tag(name = "期货")
@RestController
@RequestMapping("/futures")
public class FuturesController {
    @Autowired
    private FuturesService futuresService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "获取所有商品", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<List<FuturesAndTime>>> getAll() {
        return futuresService.getAllAndTime().map(ResponseInfo::ok);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新期货", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<Futures>> updateFutures(@RequestBody Futures futures) {
        return futuresService.updateFutures(futures).map(ResponseInfo::ok);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "新增期货", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<Futures>> addFutures(@RequestBody Futures futures) {
        return futuresService.addFutures(futures).map(ResponseInfo::ok);
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除期货", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<Futures>> deleteFutures(@RequestParam Integer id) {
        return futuresService.deleteFutures(id).map(ResponseInfo::ok);
    }

    @PostMapping("/time/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "修改期货的开盘时间", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<Tuple2<TradingTime, TradingTime>>> updateFuturesTime(@RequestBody @Valid TradingTimeUpdateReq timeReq) {
        return futuresService.updateFuturesTime(timeReq).map(ResponseInfo::ok);
    }

    /**
     * 添加期货的开盘时间
     *
     * @param timeReq 期货开盘时间
     * @return 结果
     */
    @PostMapping("/time/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "添加期货的开盘时间", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<TradingTime>> addFuturesTime(@RequestBody @Valid TradingTimeInstallReq timeReq) {
        return futuresService.addFuturesTime(timeReq).map(ResponseInfo::ok);
    }

    /**
     * 删除期货开盘时间
     *
     * @param id id
     */
    @GetMapping("/time/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除期货开盘时间", security = {@SecurityRequirement(name = "Authorization")})
    public Mono<ResponseInfo<TradingTime>> deleteFuturesTime(@RequestParam Integer id) {
        return futuresService.deleteFuturesTime(id).map(ResponseInfo::ok);
    }
}

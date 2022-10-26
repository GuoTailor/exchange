package com.exchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "测试")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "获取数据")
    @GetMapping("/test1")
    public Mono<String> test1() {
        return Mono.just("nmka");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test2")
    public String test2() {
        return "hello";
    }
}

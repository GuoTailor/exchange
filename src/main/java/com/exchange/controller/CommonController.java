package com.exchange.controller;

import com.exchange.service.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * create by GYH on 2022/10/30
 */
@Tag(name = "通用")
@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void register( ) {

    }
}

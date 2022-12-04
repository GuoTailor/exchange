package com.exchange.dto.req;

import lombok.Value;

/**
 * create by GYH on 2022/12/3
 */
@Value
public class LoginUserReq {
    String username;
    String password;
}

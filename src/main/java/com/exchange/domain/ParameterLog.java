package com.exchange.domain;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * create by GYH on 2022/9/22
 */
public record ParameterLog(
        @Id
        Integer id,

        String url,

        /**
         * 请求方法
         */
        String method,

        /**
         * 内容类型
         */
        String contentType,

        /**
         * 入参
         */
        String parameter,

        /**
         * 结果
         */
        String result,

        /**
         * 请求开始时间
         */
        LocalDateTime startTime,

        /**
         * 接口耗时
         */
        Integer duration,

        /**
         * 调用者
         */
        String caller,

        /**
         * 是否转发
         */
        Boolean isTransfer) {
}
package com.exchange.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * create by GYH on 2022/12/2
 */
@Component
@WritingConverter
public class DurationConverter implements Converter<Duration, String> {
    @Override
    public String convert(@NotNull Duration duration) {
        var days = duration.toDays();
        var hoursDuration = duration.minus(Duration.ofDays(days));
        var hours = hoursDuration.toHours();
        var minutesDuration = hoursDuration.minus(Duration.ofHours(hours));
        var minutes = minutesDuration.toMinutes();
        var secondsDuration = minutesDuration.minus(Duration.ofMinutes(minutes));
        var seconds = secondsDuration.getSeconds();
        StringBuilder sb = new StringBuilder();
        if (days != 0) sb.append(days).append(" ");
        sb.append(hours).append(":").append(minutes).append(":").append(seconds);
        return sb.toString();
    }
}

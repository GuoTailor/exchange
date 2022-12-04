package com.exchange.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * create by GYH on 2022/11/27
 */
public class DurationDeserializer extends JsonDeserializer<Duration> {
    @Override // 反序列化
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        long timestamp = jsonParser.getValueAsLong();
        return Duration.ofSeconds(timestamp);
    }
}

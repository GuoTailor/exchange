package com.exchange.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * create by GYH on 2022/11/27
 */
public class DurationSerializer extends JsonSerializer<Duration> {
    @Override // εΊεε
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        long timestamp = duration.toSeconds();
        jsonGenerator.writeNumber(timestamp);
    }
}

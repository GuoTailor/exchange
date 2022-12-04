package com.exchange.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by gyh on 2021/2/10
 */
@Configuration
public class OtherConfig {

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<>() {
            @Override
            public LocalDateTime convert(String source) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(source)), ZoneId.systemDefault());
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer())
                .serializerByType(Duration.class, new DurationSerializer())
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer())
                .deserializerByType(Duration.class, new DurationDeserializer());
    }

}

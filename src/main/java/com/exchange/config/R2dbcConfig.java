package com.exchange.config;

import com.exchange.exception.BusinessException;
import io.r2dbc.spi.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.List;

/**
 * create by GYH on 2022/12/2
 */
@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @NotNull
    @Override
    public ConnectionFactory connectionFactory() {
        throw new BusinessException("草泥马，老子没法了");
    }

    @NotNull
    @Override
    protected List<Object> getCustomConverters() {
        return List.of(new DurationConverter());
    }
}

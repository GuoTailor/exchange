package com.exchange.clinet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * create by GYH on 2022/12/20
 */
@Configuration
public class MarketHttpApiConfig {

    private final static String appcode = "你自己的AppCode";

    @Bean
    public MarketHttpApi marketHttpApi() {
        WebClient client = WebClient.builder()
                .defaultHeader("Authorization", "APPCODE " + appcode)
                .baseUrl("http://alirmcom2.market.alicloudapi.com").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();

        return factory.createClient(MarketHttpApi.class);
    }
}

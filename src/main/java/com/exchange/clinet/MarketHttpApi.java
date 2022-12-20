package com.exchange.clinet;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/12/20
 */
public interface MarketHttpApi {

    /**
     * 获取k线数据
     *
     * @param symbol 合约代码
     * @param period 周期
     * @return 数据
     */
    @GetExchange("/query/comkm4v2")
    Mono<MarketResponse> getKLine(@RequestParam String date,
                                  @RequestParam String period,
                                  @RequestParam String symbol,
                                  @RequestParam Integer withlast);
}

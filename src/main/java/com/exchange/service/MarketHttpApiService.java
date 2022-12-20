package com.exchange.service;

import com.exchange.clinet.MarketHttpApi;
import com.exchange.clinet.MarketResponse;
import com.exchange.enums.KLinePeriod;
import com.exchange.exception.BusinessException;
import com.exchange.netty.dto.PeriodKLine;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * create by GYH on 2022/12/20
 */
@Slf4j
@Service
public class MarketHttpApiService {
    @Autowired
    private MarketHttpApi marketHttpApi;
    @Autowired
    private ObjectMapper json;

    /**
     * 获取k线数据
     *
     * @param symbol 合约代码
     * @param period 周期
     * @return 数据
     */
    public Mono<List<PeriodKLine>> getKLine2(String symbol, KLinePeriod period) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return marketHttpApi.getKLine(sdf.format(new Date()), period.getLab(), symbol, 0)
                .map(it -> {
                    MarketResponse marketResponse;
                    try {
                        marketResponse = json.readValue(it, MarketResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new BusinessException(e);
                    }
                    String[] split = marketResponse.Obj().split(";");
                    ArrayList<PeriodKLine> ks = new ArrayList<>(split.length);
                    for (String k : split) {
                        PeriodKLine kLine = new PeriodKLine(k);
                        ks.add(kLine);
                    }
                    return ks;
                });
    }

}

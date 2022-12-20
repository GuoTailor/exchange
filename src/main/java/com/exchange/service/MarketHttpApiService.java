package com.exchange.service;

import com.exchange.enums.KLinePeriod;
import com.exchange.exception.BusinessException;
import com.exchange.netty.dto.PeriodKLine;
import com.exchange.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * create by GYH on 2022/12/20
 */
@Slf4j
@Service
public class MarketHttpApiService {
    private final static String appcode = "你自己的AppCode";

    /**
     * 获取k线数据
     *
     * @param symbol 合约代码
     * @param period 周期
     * @return 数据
     */
    public Mono<List<PeriodKLine>> getKLine(String symbol, KLinePeriod period) {
        String host = "http://alirmcom2.market.alicloudapi.com";
        String path = "/query/comkm4v2";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        querys.put("date", sdf.format(new Date()));
        querys.put("period", period.getLab());
        querys.put("symbol", symbol);
        querys.put("withlast", "0");


        try {
            String response = HttpUtils.doGet(host, path, headers, querys);
            String[] split = response.split(";");
            ArrayList<PeriodKLine> ks = new ArrayList<>(split.length);
            for (String k : split) {
                PeriodKLine kLine = new PeriodKLine(k);
                ks.add(kLine);
            }
            return Mono.just(ks);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new BusinessException("获取k线失败 " + e.getMessage()));
        }
    }
}

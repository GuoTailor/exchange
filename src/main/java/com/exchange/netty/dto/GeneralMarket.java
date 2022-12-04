package com.exchange.netty.dto;

import java.math.BigDecimal;

/**
 * 实时行情
 * create by GYH on 2022/11/19
 */
public record GeneralMarket(
        String Cmd,
        Integer Code,
        String Msg,
        //市场代码
        String M,
        //品种
        String S,
        //代码
        String C,
        //完整代码=(M+S+C)
        String FS,
        //中文名
        String N,
        //当日开盘价
        BigDecimal O,
        //当日最高
        BigDecimal H,
        //当日最低
        BigDecimal L,
        //当前价
        BigDecimal P,
        //昨收价
        BigDecimal YC,
        //当日成交额
        BigDecimal A,
        //当日成交量
        BigDecimal V,
        //报价时间戳
        Long Tick,
        //买一
        BigDecimal B1,
        //买二
        BigDecimal B2,
        //买三
        BigDecimal B3,
        //买四
        BigDecimal B4,
        //买五
        BigDecimal B5,
        //卖一
        BigDecimal S1,
        //卖二
        BigDecimal S2,
        //卖三
        BigDecimal S3,
        //卖四
        BigDecimal S4,
        //卖五
        BigDecimal S5,
        //买一量
        Long B1V,
        //买二量
        Long B2V,
        //买三量
        Long B3V,
        //买四量
        Long B4V,
        //买五量
        Long B5V,
        //卖一量
        Long S1V,
        //卖二量
        Long S2V,
        //卖三量
        Long S3V,
        //卖四量
        Long S4V,
        //卖五量
        Long S5V,
        //当日换手率
        BigDecimal HS,
        //当日振幅
        BigDecimal VF,
        //流通市值
        BigDecimal LS,
        //总市值
        BigDecimal ZS,
        //静态市盈率
        BigDecimal SY,
        //市净率
        BigDecimal SJ,
        //当日涨幅
        BigDecimal ZF,
        //持仓
        Long HD,
        //昨持仓
        Long YHD,
        //结算价
        BigDecimal JS,
        //昨结算价
        BigDecimal YJS,
        //当日均价/每手
        BigDecimal AVP,
        //现量
        BigDecimal NV,
        //当日内盘
        BigDecimal IV,
        //当日外盘
        BigDecimal OV,
        //当日涨停
        BigDecimal ZT,
        //当日跌停
        BigDecimal DT,
        //股本
        BigDecimal Z,
        //流通
        BigDecimal Z2,
        //动态市盈率
        BigDecimal SY2,
        //行权价
        BigDecimal QJ,
        //行权日/最后日
        String QR,
        //是否开市(1是0否)请求当时市场是否开市
        Integer MT,
        //最新成交数据 格式：秒时间戳,价格,量,方向(0表换1表多-1表空),序号(无意义),多条用分号分隔
        String TS,

        //当前各周期K线数据(动态) 格式：秒时间戳,开,高,低,量，收即是P
        String S15,
        String M1,
        String M3,
        String M5,
        String M10,
        String M15,
        String M30,
        String H1,
        String H2,
        String H4
) {
}

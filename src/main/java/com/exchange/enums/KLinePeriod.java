package com.exchange.enums;

/**
 * create by GYH on 2022/12/14
 */
public enum KLinePeriod {
    L1M("1M", 60),
    L3M("3M", 180),
    L5M("5M", 300),
    L10M("10M", 600),
    L15M("15M", 15 * 60),
    L30M("30M", 30 * 60),
    L1H("1H", 60 * 60),
    L2H("2H", 2 * 60 * 60),
    L4H("4H", 4 * 60 * 60),
    LD("D", 24 * 60 * 60),
    ;
    private final String lab;
    private final Integer duration;

    KLinePeriod(String lab, Integer duration) {
        this.lab = lab;
        this.duration = duration;
    }

    public String getLab() {
        return lab;
    }

    public Integer getDuration() {
        return duration;
    }
}

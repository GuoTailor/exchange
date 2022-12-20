package com.exchange.enums;

/**
 * create by GYH on 2022/12/14
 */
public enum KLinePeriod {
    L1M("1M"),
    L3M("3M"),
    L5M("5M"),
    L10M("10M"),
    L15M("15M"),
    L30M("30M"),
    L1H("1H"),
    L2H("2H"),
    L4H("4H"),
    LD("D"),
    ;
    private final String lab;

    KLinePeriod(String lab) {
        this.lab = lab;
    }

    public String getLab() {
        return lab;
    }
}

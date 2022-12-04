package com.exchange.domain;

import com.exchange.enums.FuturesTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.Duration;

@Data
public class CloseOutTime {
    @Id
    private Integer id;

    /**
     * 强平时间
     */
    private Duration time;

    /**
     * 国内外类型
     */
    private FuturesTypeEnum type;

    /**
     * 备注
     */
    private String remark;

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
    }
}


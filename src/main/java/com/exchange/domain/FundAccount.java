package com.exchange.domain;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data
@Accessors(chain = true)
public class FundAccount {
    @Id
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 余额
     */
    private BigDecimal balance;

    @Version
    private Integer version;
}


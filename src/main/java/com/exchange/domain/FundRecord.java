package com.exchange.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.exchange.enums.AuditStatus;
import com.exchange.enums.DepositTypeEnum;
import com.exchange.enums.DepositWithdrawTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

/**
 * 充提记录
 */
@Data
@Accessors(chain = true)
public class FundRecord {

    @Id
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 类型
     */
    private DepositWithdrawTypeEnum type;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 时间
     */
    private LocalDateTime createTime;

    /**
     * 审核状态
     */
    private AuditStatus state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 充值类型
     */
    private DepositTypeEnum depositType;

    /**
     * true删除
     */
    private Boolean deleteFlag;
}


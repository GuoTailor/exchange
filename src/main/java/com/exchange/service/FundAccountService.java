package com.exchange.service;

import com.exchange.domain.FundAccount;
import com.exchange.domain.FundRecord;
import com.exchange.domain.User;
import com.exchange.dto.PageInfo;
import com.exchange.dto.req.AuditInfoReq;
import com.exchange.dto.req.DepositReq;
import com.exchange.dto.req.FundRecordPageReq;
import com.exchange.enums.AuditStatus;
import com.exchange.enums.DepositWithdrawTypeEnum;
import com.exchange.exception.BusinessException;
import com.exchange.mapper.FundAccountMapper;
import com.exchange.mapper.FundRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * create by GYH on 2022/11/20
 */
@Slf4j
@Service
public class FundAccountService {
    @Resource
    private FundAccountMapper fundAccountMapper;
    @Resource
    private FundRecordMapper fundRecordMapper;

    public Mono<FundAccount> getBalance() {
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> fundAccountMapper.findByUserId(it)
                        .switchIfEmpty(Mono.defer(() -> createAccount(it)))
                );
    }

    /**
     * 创建用户资金账户
     *
     * @param userId 用户id
     * @return 结果
     */
    public Mono<FundAccount> createAccount(Integer userId) {
        FundAccount account = new FundAccount();
        account.setBalance(BigDecimal.ZERO);
        account.setUserId(userId);
        return fundAccountMapper.save(account);
    }

    /**
     * 充值
     *
     * @param req 充值实体
     */
    public Mono<FundRecord> deposit(DepositReq req) {
        FundRecord fundRecord = new FundRecord()
                .setType(DepositWithdrawTypeEnum.DEPOSIT)
                .setMoney(req.getMoney())
                .setCreateTime(LocalDateTime.now())
                .setState(AuditStatus.AUDIT)
                .setDepositType(req.getDepositType());
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> {
                    fundRecord.setUserId(it);
                    return fundRecordMapper.save(fundRecord);
                });
    }

    /**
     * 提现
     *
     * @param money 提现金额
     */
    public Mono<FundRecord> withdraw(BigDecimal money) {
        FundRecord fundRecord = new FundRecord()
                .setType(DepositWithdrawTypeEnum.WITHDRAW)
                .setMoney(money)
                .setCreateTime(LocalDateTime.now())
                .setState(AuditStatus.AUDIT);
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> {
                    fundRecord.setUserId(it);
                    return fundRecordMapper.save(fundRecord);
                });
    }

    /**
     * 审核
     *
     * @param auditInfoReq 请求实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Mono<FundRecord> audit(AuditInfoReq auditInfoReq) {
        return fundRecordMapper.findByIdForUpdate(auditInfoReq.getId())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("充提记录不存在 " + auditInfoReq.getId())))
                .filter(it -> it.getState() != AuditStatus.REFUSE)
                .switchIfEmpty(Mono.error(new BusinessException("充提记录已经审核通过不能再次审核 " + auditInfoReq.getId())))
                .zipWhen(it -> fundAccountMapper.findByUserId(it.getUserId()).defaultIfEmpty(new FundAccount().setUserId(it.getUserId()).setBalance(BigDecimal.ZERO)))
                .flatMap(it -> {
                    FundAccount account = it.getT2();
                    FundRecord record = it.getT1();
                    record.setState(auditInfoReq.getState());
                    if (auditInfoReq.getState() == AuditStatus.PASS) {
                        // 如果是充值就加钱
                        if (record.getType() == DepositWithdrawTypeEnum.DEPOSIT) {
                            account.setBalance(account.getBalance().add(record.getMoney()));
                        } else if (account.getBalance().compareTo(record.getMoney()) >= 0) {
                            // 如果是提现并且余额大于等于提现金额就减钱
                            account.setBalance(account.getBalance().subtract(record.getMoney()));
                        } else {
                            // 否则就是余额不足
                            return Mono.error(new BusinessException("余额不足"));
                        }
                    } else if (auditInfoReq.getState() == AuditStatus.REFUSE) {
                        record.setRemark(auditInfoReq.getRemark());
                    } else {
                        return Mono.error(new BusinessException("审核状态不能设置为" + AuditStatus.AUDIT));
                    }
                    return fundAccountMapper.save(account)
                            .flatMap(r -> fundRecordMapper.save(record));
                });
    }

    /**
     * 分页查询提现充值记录
     *
     * @param pageReq 分页实体
     * @return 充值提现记录
     */
    public Mono<PageInfo<FundRecord>> selectPage(FundRecordPageReq pageReq) {
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> {
                    Example<FundRecord> example = Example.of(new FundRecord().setUserId(it));
                    return fundRecordMapper.findByUserId(it, (pageReq.getPage() - 1) * pageReq.getPageSize(), pageReq.getPageSize())
                            .collectList()
                            .zipWith(fundRecordMapper.count(example), (data, count) -> PageInfo.ok(count, pageReq, data));
                });
    }

    /**
     * 添加钱
     *
     * @param money 钱
     * @return 结果
     */
    public Mono<Integer> add(Integer userId, BigDecimal money) {
        return fundAccountMapper.addMoney(userId, money);
    }

    /**
     * 减少钱
     *
     * @param money 钱
     * @return 结果
     */
    public Mono<Integer> sub(Integer userId, BigDecimal money) {
        return fundAccountMapper.subMoney(userId, money);
    }
}

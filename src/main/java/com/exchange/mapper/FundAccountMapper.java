package com.exchange.mapper;

import com.exchange.domain.FundAccount;
import com.exchange.domain.FundRecord;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * 资金账户
 * create by GYH on 2022/11/20
 */
public interface FundAccountMapper extends R2dbcRepository<FundAccount, Integer> {

    @Query("select * from fund_account where user_id = :userId")
    Mono<FundAccount> findByUserId(Integer userId);

    @Query("select * from fund_account where user_id = :userId for update ")
    Mono<FundAccount> findByUserIdForUpdate(Integer userId);

    @Query("update fund_account set balance = balance + :money where user_id = :userId and :money > 0")
    @Modifying
    Mono<Integer> addMoney(Integer userId, BigDecimal money);

    @Query("update fund_account set balance = balance - :money where user_id = :userId and balance - :money > 0")
    @Modifying
    Mono<Integer> subMoney(Integer userId, BigDecimal money);

}

package com.exchange.mapper;

import com.exchange.domain.FundRecord;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * 充提记录
 * create by GYH on 2022/11/20
 */
public interface FundRecordMapper extends R2dbcRepository<FundRecord, Integer> {
    @Query("select * from fund_record where id = :id for update ")
    Mono<FundRecord> findByIdForUpdate(Integer id);

    @Query("select * from fund_record where user_id = :userId order by create_time desc limit :limit offset :offset ")
    Flux<FundRecord> findByUserId(Integer userId, Integer offset, Integer limit);
}

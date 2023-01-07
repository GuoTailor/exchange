package com.exchange.mapper;

import com.exchange.domain.Invitation;
import com.exchange.dto.resp.InvitationInfo;
import com.exchange.enums.InvitationStateEnum;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2023/1/4
 */
public interface InvitationMapper extends R2dbcRepository<Invitation, Integer> {

    @Query("select * from invitation where invitation_code = :invitationCode")
    Mono<Invitation> findByBusinessId(String businessId);

    @Query("select i.*, u1.username as account, u2.username as creator from invitation i " +
            " left join user u1 on u1.id = i.user_id " +
            " left join user u2 on u2.id = i.business_id " +
            " where (i.business_id = :userId" +
            " and i.state = :state)" +
            " and (u1.username like concat('%', :keyword, '%') or u2.username like concat('%', :keyword, '%'))" +
            " order by i.create_time desc limit :limit offset :offset ")
    Flux<InvitationInfo> findByPage(Integer userId, InvitationStateEnum state, String keyword, Integer offset, Integer limit);

    @Query("select count(i.id) from invitation i " +
            " left join user u1 on u1.id = i.user_id " +
            " left join user u2 on u2.id = i.business_id " +
            " where (i.business_id = :userId" +
            " and i.state = :state)" +
            " and (u1.username like concat('%', :keyword, '%') or u2.username like concat('%', :keyword, '%'))")
    Mono<Long> countByPage(Integer userId, InvitationStateEnum state, String keyword);

    @Query("select i.*, u1.username as account, u2.username as creator from invitation i " +
            " left join user u1 on u1.id = i.user_id " +
            " left join user u2 on u2.id = i.business_id " +
            " where i.state = :state" +
            " and (u1.username like concat('%', :keyword, '%') or u2.username like concat('%', :keyword, '%'))" +
            " order by i.create_time desc limit :limit offset :offset ")
    Flux<InvitationInfo> findAllByPage(InvitationStateEnum state, String keyword, Integer offset, Integer limit);

    @Query("select count(i.id) from invitation i " +
            " left join user u1 on u1.id = i.user_id " +
            " left join user u2 on u2.id = i.business_id " +
            " where i.state = :state" +
            " and (u1.username like concat('%', :keyword, '%') or u2.username like concat('%', :keyword, '%'))")
    Mono<Long> countAllByPage(InvitationStateEnum state, String keyword);
}

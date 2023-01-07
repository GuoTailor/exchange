package com.exchange.mapper;

import com.exchange.domain.Role;
import com.exchange.domain.User;
import com.exchange.dto.req.UserInfoPageReq;
import com.exchange.dto.resp.UserInfo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/10/28
 */
public interface UserMapper extends R2dbcRepository<User, Integer> {

    @Query("select * from user where username = :username")
    Mono<User> findByUsername(String username);

    @Query("select role.* from role left join user_role ur on role.id = ur.role_id where user_id = :userId")
    Flux<Role> findRolesByUserId(Integer userId);

    @Query("insert into user_role(user_id, role_id) values (:userId, :roleId)")
    @Modifying
    Mono<Integer> saveUserRoleRelation(Integer userId, Integer roleId);

    @Query("select u.*, rn.state, fa.balance" +
            " from user u" +
            " left join real_name rn on u.id = rn.user_id" +
            " left join fund_account fa on u.id = fa.user_id" +
            " where u.enable = :#{[0].enable}")
    Flux<UserInfo> findAllByPage(UserInfoPageReq pageReq);
}

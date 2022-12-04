package com.exchange.mapper;

import com.exchange.domain.Role;
import com.exchange.domain.User;
import org.springframework.data.domain.Pageable;
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

    @Query("delete from user where username = 'sad'")
    Flux<User> findAllBy(Pageable pageable);
}

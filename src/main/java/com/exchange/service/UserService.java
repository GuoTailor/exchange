package com.exchange.service;

import com.exchange.domain.User;
import com.exchange.dto.resp.UserInfo;
import com.exchange.mapper.FundAccountMapper;
import com.exchange.mapper.RealNameMapper;
import com.exchange.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * create by GYH on 2022/10/28
 */
@Service
public class UserService implements ReactiveUserDetailsService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RealNameMapper realNameMapper;
    @Resource
    private FundAccountMapper fundAccountMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userMapper.findByUsername(username)
                .zipWhen(it -> userMapper.findRolesByUserId(it.getId()).collectList())
                .map(it -> {
                    it.getT1().setRoles(it.getT2());
                    return it.getT1();
                })
                .cast(UserDetails.class);
    }

    public Mono<User> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.save(user)
                .zipWhen(it -> userMapper.saveUserRoleRelation(it.getId(), 1))
                .map(Tuple2::getT1);
    }

    public Mono<Page<User>> getUsers(PageRequest pageRequest) {
        return userMapper.findAllBy(pageRequest)
                .collectList()
                .zipWith(userMapper.count())
                .map(it -> new PageImpl<>(it.getT1(), pageRequest, it.getT2()));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public Mono<UserInfo> getUserInfo() {
        UserInfo userInfo = new UserInfo();
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .flatMap(it -> {
                    userInfo.setId(it);
                    return userMapper.findById(it);
                }).flatMap(it -> {
                    userInfo.setUsername(it.getUsername());
                    return realNameMapper.isRealName(it.getId());
                }).flatMap(it -> {
                    userInfo.setRealName(it != null);
                    return fundAccountMapper.findByUserId(userInfo.getId());
                }).map(it -> {
                    userInfo.setBalance(it.getBalance());
                    return userInfo;
                });
    }
}

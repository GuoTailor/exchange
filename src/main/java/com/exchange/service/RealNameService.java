package com.exchange.service;

import com.exchange.domain.RealName;
import com.exchange.domain.User;
import com.exchange.dto.req.RealNameAuditReq;
import com.exchange.dto.req.RealNameReq;
import com.exchange.enums.RealNameAuthEnum;
import com.exchange.exception.BusinessException;
import com.exchange.mapper.RealNameMapper;
import com.exchange.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * create by GYH on 2022/11/19
 */
@Service
public class RealNameService {
    @Resource
    private RealNameMapper realNameMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 添加实名记录
     *
     * @param realNameReq 实体
     * @return 实体
     */
    public Mono<RealName> addRecord(RealNameReq realNameReq) {
        RealName realName = new RealName();
        BeanUtils.copyProperties(realNameReq, realName);

        realName.setState(RealNameAuthEnum.UNDER_REVIEW);
        realName.setCreateTime(LocalDateTime.now());
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .map(it -> {
                    realName.setUserId(it);
                    return userMapper.findById(it);
                })
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在 " + realName.getUserId())))
                .flatMap(it -> realNameMapper.save(realName));
    }

    /**
     * 审核
     *
     * @param realNameAuditReq 请求实体
     */
    public Mono<RealName> audit(RealNameAuditReq realNameAuditReq) {
        return realNameMapper
                .findById(realNameAuditReq.getId())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new BusinessException("实名信息不存在 " + realNameAuditReq.getId())))
                .map(it -> {
                    it.setState(realNameAuditReq.getState());
                    it.setRemark(realNameAuditReq.getRemark());
                    it.setUpdateTime(LocalDateTime.now());
                    return it;
                }).flatMap(it -> realNameMapper.save(it));
    }

}

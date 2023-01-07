package com.exchange.service;

import com.exchange.domain.Invitation;
import com.exchange.domain.Role;
import com.exchange.domain.User;
import com.exchange.dto.PageInfo;
import com.exchange.dto.req.InvitationPageReq;
import com.exchange.dto.resp.InvitationInfo;
import com.exchange.enums.InvitationStateEnum;
import com.exchange.exception.BusinessException;
import com.exchange.mapper.InvitationMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2023/1/4
 */
@Slf4j
@Service
public class InvitationService {
    private static final String invitationKey = "invitation";
    @Resource
    private InvitationMapper invitationMapper;
    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    public Mono<Invitation> createInvitation() {
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()).getId())
                .zipWhen(it -> redisTemplate.opsForValue().increment(invitationKey))
                .flatMap(it -> {
                    StringBuilder sb = new StringBuilder(it.getT2().toString());
                    while (sb.length() < 6) {
                        sb.insert(0, 0);
                    }
                    Invitation invitation = new Invitation();
                    invitation.setBusinessId(it.getT1());
                    invitation.setInvitationCode(sb.toString());
                    invitation.setState(InvitationStateEnum.UNUSED);
                    return invitationMapper.findByBusinessId(sb.toString())
                            .<Invitation>map(invitat -> {
                                throw new BusinessException("邀请码已经存在");
                            }).switchIfEmpty(Mono.just(invitation));
                })
                .retry()
                .flatMap(it -> invitationMapper.save(it));
    }

    public Mono<PageInfo<InvitationInfo>> pageList(InvitationPageReq req) {
        return ReactiveSecurityContextHolder.getContext()
                .map(it -> ((User) it.getAuthentication().getPrincipal()))
                .flatMap(it -> {
                    if (it.getStringRoles().contains(Role.ADMIN)) {
                        return invitationMapper.findAllByPage(req.getUseState(), req.getKeyword(), (req.getPage() - 1) * req.getPageSize(), req.getPageSize()).collectList()
                                .zipWith(invitationMapper.countAllByPage(req.getUseState(), req.getKeyword()), (data, count) -> PageInfo.ok(count, req, data));
                    } else {
                        return invitationMapper.findByPage(it.getId(), req.getUseState(), req.getKeyword(), (req.getPage() - 1) * req.getPageSize(), req.getPageSize()).collectList()
                                .zipWith(invitationMapper.countByPage(it.getId(), req.getUseState(), req.getKeyword()), (data, count) -> PageInfo.ok(count, req, data));
                    }
                });

    }
}

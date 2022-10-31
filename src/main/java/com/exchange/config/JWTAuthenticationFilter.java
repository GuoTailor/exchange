package com.exchange.config;

import com.exchange.domain.User;
import com.exchange.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * create by GYH on 2022/10/28
 */
@Slf4j
public class JWTAuthenticationFilter implements WebFilter {

    private static final Log logger = LogFactory.getLog(AuthorizationWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        List<String> strings = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        log.info("{}", strings);
        if (!CollectionUtils.isEmpty(strings)) {
            for (String authHeader : strings) {
                if (authHeader.startsWith("Bearer ")) {
                    final String authToken = authHeader.replaceFirst("Bearer ", "");
                    User user = JwtUtil.parseToken(authToken, new User());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
                    logger.info("authenticated user " + user.getUsername() + ", setting security context");
                    ReactiveSecurityContextHolder.withAuthentication(authentication);
//                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    return ReactiveSecurityContextHolder.getContext().map(it -> {
                        it.setAuthentication(authentication);
                        return it;
                    }).switchIfEmpty(Mono.fromFuture(() -> {

                            }))
                            .then();
                }
            }
        }
        return chain.filter(exchange);
    }
}

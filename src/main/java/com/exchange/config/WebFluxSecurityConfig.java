package com.exchange.config;

import com.exchange.domain.Role;
import com.exchange.domain.User;
import com.exchange.dto.ResponseInfo;
import com.exchange.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

/**
 * create by GYH on 2022/10/24
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
public class WebFluxSecurityConfig {
    private final ObjectMapper json = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .securityContextRepository(new TokenServerSecurityContextRepository())
                .exceptionHandling()
                .accessDeniedHandler((ServerWebExchange exchange, AccessDeniedException denied) -> {
                    ServerHttpResponse response = exchange.getResponse();
                    log.info("授权失败 {}", exchange.getRequest().getURI());
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    try {
                        return response.writeWith(Mono.just(response.bufferFactory().wrap(json.writeValueAsBytes(ResponseInfo.failed(denied.getMessage())))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).and()
                .formLogin()
                .authenticationSuccessHandler((WebFilterExchange webFilterExchange, Authentication authentication) -> {
                    log.info("登录成功 {}", authentication);
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) authentication;
                    String token = JwtUtil.generateToken((User) user.getPrincipal());
                    try {
                        return response.writeWith(Mono.just(response.bufferFactory().wrap(json.writeValueAsBytes(ResponseInfo.ok("成功", token)))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .authenticationFailureHandler((WebFilterExchange webFilterExchange, AuthenticationException exception) -> {
                    log.info("登录失败", exception);
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    try {
                        return response.writeWith(Mono.just(response.bufferFactory().wrap(json.writeValueAsBytes(ResponseInfo.failed("用户名或密码错误")))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .and()
                .logout().disable()
                .authorizeExchange()
                .pathMatchers("/common/**", "/login", "/swagger-ui.html",
                        "/swagger-ui/*", "/swagger-resources/**", "/v3/api-docs/**", "/webjars/**",
                        "/*/*.html", "/*/*.js", "/*/*.css", "/*/*.png", "/*.ico", "/static/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .build();
    }

    /**
     * 添加角色继承关系
     *
     * @return RoleHierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy(DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler) {
        var hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(Role.SUPER_ADMIN + " > " + Role.ADMIN + " > " + Role.USER);
        methodSecurityExpressionHandler.setRoleHierarchy(hierarchy);
        return hierarchy;
    }

}

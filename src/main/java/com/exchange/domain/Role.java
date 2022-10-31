package com.exchange.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

/**
 * create by GYH on 2022/10/27
 */
@Data
public class Role implements GrantedAuthority {
    @Id
    private Integer id;
    private String authority;
    private String roleName;

    public Role() {
    }

    public Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}

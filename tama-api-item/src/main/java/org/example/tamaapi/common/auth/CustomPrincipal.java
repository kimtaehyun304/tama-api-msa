package org.example.tamaapi.common.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Collection;

@Getter
public class CustomPrincipal {
    private final String jwt;
    private final Long memberId;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomPrincipal(String jwt, Long userId, Collection<? extends GrantedAuthority> authorities) {
        this.jwt = jwt;
        this.memberId = userId;
        this.authorities = authorities;
    }

}
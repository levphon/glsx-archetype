package com.glsx.plat.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class TokenAuthentication extends UsernamePasswordAuthenticationToken {

    private final String token;

    public TokenAuthentication(String token, Object principal, Object credentials) {
        super(principal, credentials);
        this.token = token;
    }

    public TokenAuthentication(String token, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.token = token;
    }

}

package org.froome.paymentservice.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {
    private final String token;

    public BearerTokenAuthentication(String token) {
        super(null);
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}

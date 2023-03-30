package com.revature.project2backend.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

public class CustomCookieCsrfTokenRepository implements CsrfTokenRepository {

    private final CookieCsrfTokenRepository delegate;
    private final String csrfCookieName;

    public CustomCookieCsrfTokenRepository() {
        this.delegate = new CookieCsrfTokenRepository();
        this.delegate.setCookieHttpOnly(false);
        this.csrfCookieName = "XSRF-TOKEN";
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return delegate.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = token == null ? "" : token.getToken();
        response.setHeader("Set-Cookie", String.format("%s=%s; Path=/; Secure; SameSite=None", csrfCookieName, tokenValue));
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return delegate.loadToken(request);
    }
}

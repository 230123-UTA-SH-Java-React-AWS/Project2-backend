package com.revature.project2backend.security;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        //check for presence of jwt in "Authorization" header, validate the token, extract the email address within, load the user details associated with the email then set the authentication info in the security context
        String jwtToken = getJwtFromRequest(request);

        if(StringUtils.hasText(jwtToken) && jwtGenerator.validateToken(jwtToken) ){
            String email = jwtGenerator.getEmailFromJwt(jwtToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}

/*
* JwtAuthFilter Class Documentation
This class extends the OncePerRequestFilter to implement a custom JWT authentication filter for the application. It checks for the presence of a JWT token in the "Authorization" header, validates the token, extracts the email address, loads the user details associated with the email, and sets the authentication information in the security context.

Dependencies
JwtGenerator: A utility class for generating, parsing, and validating JWT tokens.
CustomUserDetailsService: A custom user details service for loading user-specific data.
Methods
doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
This method is invoked once per request and overrides the doFilterInternal method of OncePerRequestFilter. It checks for the presence of a JWT token in the "Authorization" header, validates the token, extracts the email address within, loads the user details associated with the email, and then sets the authentication information in the security context.

getJwtFromRequest(HttpServletRequest request)
This private method takes an HttpServletRequest object as a parameter and extracts the JWT token from the "Authorization" header. If the header contains a valid JWT token with the "Bearer" prefix, the method returns the token. If the header does not contain a valid token, the method returns null.

Annotations
@Autowired: Indicates that the annotated class member should be auto-wired by Spring's dependency injection.
Extends
OncePerRequestFilter: A base class for filters that should be executed once per request.
* */
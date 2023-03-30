package com.revature.project2backend.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create a JSON object with a custom error message
        String jsonResponse = "{\"error\":\"JWT is not present or invalid\"}";

        // Write the JSON response to the HttpServletResponse
        response.getWriter().write(jsonResponse);
    }
}

/*
* JwtAuthEntryPoint Class Documentation
This class implements the AuthenticationEntryPoint interface and serves as a custom JWT authentication entry point for the application. It handles requests that fail to authenticate due to missing or invalid JWT tokens.

Dependencies
None.

Methods
commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
This method is invoked when an authentication request fails due to an invalid or missing JWT token. It sets the response status to HttpServletResponse.SC_UNAUTHORIZED, sets the content type to "application/json", and sends a custom JSON error message in the response body.

Annotations
@Component: Indicates that the class should be treated as a Spring component and managed by the Spring container.
Implements
AuthenticationEntryPoint: An interface for handling authentication requests that fail for any reason.
* */
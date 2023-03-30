package com.revature.project2backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtGenerator {

    public String generateJwtToken(Authentication authentication){
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
        String jwtToken = Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(expireDate).signWith(SignatureAlgorithm.HS256, SecurityConstants.JWT_SECRET).compact();
        return jwtToken;
    }

    public String getEmailFromJwt(String jwtToken){
        Claims claims = Jwts.parserBuilder().setSigningKey(SecurityConstants.JWT_SECRET).build().parseClaimsJws(jwtToken).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String jwtToken){
        try{
            Jwts.parserBuilder().setSigningKey(SecurityConstants.JWT_SECRET).build().parseClaimsJws(jwtToken);
            return true;
        }catch(Exception ex){
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect!");
        }
    }
}

/*
* JwtGenerator Class Documentation
This class provides utility methods for generating, parsing, and validating JSON Web Tokens (JWT) in the application. It uses the io.jsonwebtoken.Jwts library for handling JWTs.

Dependencies
io.jsonwebtoken.Claims: Represents the claims of a JWT.
io.jsonwebtoken.Jwts: Provides utility methods for JWT generation and parsing.
io.jsonwebtoken.SignatureAlgorithm: Enumerates JWT signing algorithms.
SecurityConstants: A class containing security-related constants.
Methods
generateJwtToken(Authentication authentication)
This method takes an Authentication object and generates a JWT token for the authenticated user. It sets the subject to the user's email, the issue date to the current date, and the expiration date to the current date plus the JWT_EXPIRATION constant from SecurityConstants. The token is then signed with the HS256 algorithm and the JWT_SECRET constant from SecurityConstants.

Returns: A JWT token as a String.

getEmailFromJwt(String jwtToken)
This method takes a JWT token as a String and extracts the user's email from the token's subject claim.

Returns: The user's email as a String.

validateToken(String jwtToken)
This method takes a JWT token as a String and validates it by checking its signature and expiration date using the JWT_SECRET constant from SecurityConstants. If the token is valid, it returns true. If the token is invalid or expired, it throws an AuthenticationCredentialsNotFoundException with an appropriate message.

Returns: A boolean value indicating whether the JWT token is valid or not.

Annotations
@Component: Indicates that the class is a Spring component, making it available for dependency injection.
* */
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

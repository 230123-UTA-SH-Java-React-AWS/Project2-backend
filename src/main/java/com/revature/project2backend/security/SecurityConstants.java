package com.revature.project2backend.security;

public class SecurityConstants {
//    TODO: Add to environment variables

    // how long jwt token is valid for, currently 3 days
    public static final long JWT_EXPIRATION = 259200000;
    // 256 bit secret key for token generator
    public static final String JWT_SECRET = "703373367638792F423F4528482B4D6251655468576D5A7134743777217A2443";
}

package com.revature.project2backend.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String username;
    private String email;
    private String accessToken;
    private String tokenType = "Bearer ";

    public AuthResponseDto(String username, String email, String accessToken){
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
    }
}
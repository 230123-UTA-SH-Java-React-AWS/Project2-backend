package com.revature.project2backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.service.impl.UserServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;


    @Test
    void login_validCredentials() throws Exception {

        LoginDto loginDto = new LoginDto("test@email.com", "password");
        AuthResponseDto authResponseDto = new AuthResponseDto("testuser", "test@email.com", "jwt-token");
        when(userService.login(loginDto)).thenReturn(ResponseEntity.ok(authResponseDto));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).login(loginDto);
    }

    @Test
    void login_invalidCredentials() throws Exception {
        LoginDto loginDto = new LoginDto("invalid@email.com", "wrong_password");

        when(userService.login(loginDto)).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());

        verify(userService).login(loginDto);
    }

    // Add more tests for register and getCurrentUser endpoints
}
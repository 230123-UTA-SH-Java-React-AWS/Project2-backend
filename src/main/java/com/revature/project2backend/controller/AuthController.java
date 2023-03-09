package com.revature.project2backend.controller;


import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import com.revature.project2backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserServiceImpl userService;

    @Autowired
    public AuthController( UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        return userService.register(registerDto);
    }

}

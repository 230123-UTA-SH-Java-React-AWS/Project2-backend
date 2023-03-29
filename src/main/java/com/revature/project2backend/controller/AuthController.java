package com.revature.project2backend.controller;


import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import com.revature.project2backend.dto.UserDto;
import com.revature.project2backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserServiceImpl userService;

    @Autowired
    public AuthController( UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        return userService.register(registerDto);
    }

    @GetMapping("user")
    public Optional<UserDto> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/check-email/{email}")
    public boolean checkEmail(@PathVariable("email") String email) {
       return userService.checkEmailExists(email);
    }

    @GetMapping("/check-username/{username}")
public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
    boolean isUnique = userService.isUsernameUnique(username);
    return ResponseEntity.ok(isUnique);
}

}

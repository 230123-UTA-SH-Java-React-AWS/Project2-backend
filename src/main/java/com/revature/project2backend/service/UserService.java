package com.revature.project2backend.service;



import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    ResponseEntity<?> login(@RequestBody LoginDto loginDto);

    ResponseEntity<String> register(@RequestBody RegisterDto registerDto);
}

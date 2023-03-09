package com.revature.project2backend.service.impl;

import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import com.revature.project2backend.model.UserEntity;
import com.revature.project2backend.repository.UserRepository;
import com.revature.project2backend.security.JwtGenerator;
import com.revature.project2backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserServiceImpl implements UserService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtGenerator jwtGenerator;


    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        try{
            // authenticate user with a token that's passed to the authentication manager in Spring
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
            // store authenticated user in Spring Security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // generate jwt token for the user and send it as a response
            String jwtToken = jwtGenerator.generateJwtToken(authentication);
            return new ResponseEntity<>(new AuthResponseDto(jwtToken), HttpStatus.OK);
        } catch (AuthenticationException e) {
            // returns a 'bad credentials' message when email or password is wrong
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        // check if email already exists
        if(userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email is taken!", HttpStatus.BAD_REQUEST);
        }
        // check if the username already exists
        if(userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }
        // create user instance and set credentials
        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        // hash the password then set it
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // save the user to the database
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!",HttpStatus.OK);

    }
}

package com.revature.project2backend.service.impl;

import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import com.revature.project2backend.dto.UserDto;
import com.revature.project2backend.exception.LoginNotValidException;
import com.revature.project2backend.exception.RegisterNotValidException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

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

    //TODO: clean up response to include user details like username, wins, and profile photo
    public Optional<UserDto> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String userEmail = ((UserDetails) principal).getUsername();
            Optional<UserEntity> user = userRepository.findByEmail(userEmail);
            if (user.isPresent()) {
                UserDto userDTO = new UserDto();
                userDTO.setEmail(user.get().getEmail());
                userDTO.setUsername(user.get().getUsername());
                return Optional.of(userDTO);
            }
        }
        return Optional.empty();
    }

    //TODO: include user details like username, wins, and profile photo
    @Override
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        if(Boolean.TRUE.equals((userRepository.existsByEmail(loginDto.getEmail())))){
            Optional<UserEntity> user = userRepository.findByEmail(loginDto.getEmail());
            if (user.isPresent() && passwordEncoder.matches( loginDto.getPassword(),user.get().getPassword())) {
                // authenticate user with email & password that's passed to the authentication manager in Spring
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
                // store authenticated user in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // generate jwt token for the user and send it as a response
                String jwtToken = jwtGenerator.generateJwtToken(authentication);
                return new ResponseEntity<>(new AuthResponseDto(user.get().getUsername(), loginDto.getEmail(),jwtToken ), HttpStatus.OK);
            } else{
                throw new LoginNotValidException("Invalid password!");
            }
        } else{
            throw new LoginNotValidException("Email doesn't exist!");
        }
    }

    @Override
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        // check if email already exists
        if(Boolean.TRUE.equals(userRepository.existsByEmail(registerDto.getEmail()))){
            throw new RegisterNotValidException("Email is already in use!");
        }
        // check if the username already exists
        if(Boolean.TRUE.equals(userRepository.existsByUsername(registerDto.getUsername()))){
            throw new RegisterNotValidException("Username is already in use!");
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

package com.revature.project2backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.revature.project2backend.dto.UserDto;
import com.revature.project2backend.exception.LoginNotValidException;
import com.revature.project2backend.exception.RegisterNotValidException;
import com.revature.project2backend.service.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.dto.RegisterDto;
import com.revature.project2backend.model.UserEntity;
import com.revature.project2backend.repository.UserRepository;
import com.revature.project2backend.security.JwtGenerator;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtGenerator jwtGenerator;
    @Mock
    EmailTokenServiceImpl emailTokenService;
    @Mock
    EmailSender emailSender;
    @InjectMocks
    UserServiceImpl userService;
    
    LoginDto loginDto;
    UserEntity userEntity;
    UserDetails userDetails;
    Authentication authentication;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setEmail("bogus@email.com");
        loginDto.setPassword("crazyPassword1!");
        
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("bogus");
        userEntity.setEmail("bogus@email.com");
        userEntity.setPassword("hashed_password");
        userEntity.setEnabled(true);


        userDetails = new User(userEntity.getEmail(), userEntity.getPassword(), new ArrayList<>());
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
    }

    // Login Testing

    @Test
    void login_success(){

        when(userRepository.existsByEmail(loginDto.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtGenerator.generateJwtToken(authentication)).thenReturn("dummy_token");


        ResponseEntity<AuthResponseDto> response = userService.login(loginDto);


        assertEquals(200, response.getStatusCodeValue());
        assertEquals("bogus", response.getBody().getUsername());
        assertEquals("bogus@email.com", response.getBody().getEmail());
        assertEquals("dummy_token", response.getBody().getAccessToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtGenerator, times(1)).generateJwtToken(authentication);
    }

    @Test
    void login_failure() {

        when(userRepository.existsByEmail(loginDto.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).thenReturn(false);

        assertThrows(LoginNotValidException.class, () -> userService.login(loginDto));

        verify(userRepository, times(1)).existsByEmail(loginDto.getEmail());
        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), userEntity.getPassword());
    }
    @Test
    void login_userNotFound() {
        when(userRepository.existsByEmail(loginDto.getEmail())).thenReturn(false);

        assertThrows(LoginNotValidException.class, () -> userService.login(loginDto));

        verify(userRepository, times(1)).existsByEmail(loginDto.getEmail());
        verify(userRepository, times(0)).findByEmail(loginDto.getEmail());
    }
    @Test
    void login_wrongPassword() {
        when(userRepository.existsByEmail(loginDto.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).thenReturn(false);

        assertThrows(LoginNotValidException.class, () -> userService.login(loginDto));

        verify(userRepository, times(1)).existsByEmail(loginDto.getEmail());
        verify(userRepository, times(1)).findByEmail(loginDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginDto.getPassword(), userEntity.getPassword());
    }

    //register

    @Test
    void register_success(){

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("bogus");
        registerDto.setEmail("bogus@email.com");
        registerDto.setPassword("crazyPassword1!");

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("hashed_password");


        ResponseEntity<String> response = userService.register(registerDto);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered. PLease confirm your email by clicking the link sent to: bogus@email.com", response.getBody());

        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(passwordEncoder, times(1)).encode(registerDto.getPassword());
    }

    @Test
    void register_emailExists() {

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("bogus");
        registerDto.setEmail("bogus@email.com");
        registerDto.setPassword("crazyPassword1!");

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        assertThrows(RegisterNotValidException.class, () -> userService.register(registerDto));

        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
    }

    @Test
    void register_usernameExists() {

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("bogus");
        registerDto.setEmail("bogus@email.com");
        registerDto.setPassword("crazyPassword1!");

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(true);

        assertThrows(RegisterNotValidException.class, () -> userService.register(registerDto));

        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());
    }

    @Test
    void register_saveFailure() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("bogus");
        registerDto.setEmail("bogus@email.com");
        registerDto.setPassword("crazyPassword1!");

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Error saving user"));

        assertThrows(RuntimeException.class, () -> userService.register(registerDto));

        verify(userRepository, times(1)).existsByEmail(registerDto.getEmail());
        verify(userRepository, times(1)).existsByUsername(registerDto.getUsername());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(passwordEncoder, times(1)).encode(registerDto.getPassword());
    }

    //jwt login

    @Test
    void getCurrentUser_success() {

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("bogus@email.com", "crazyPassword1!", new ArrayList<>());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("bogus@email.com")).thenReturn(Optional.of(userEntity));


        Optional<UserDto> result = userService.getCurrentUser();


        assertTrue(result.isPresent());
        assertEquals("bogus", result.get().getUsername());
        assertEquals("bogus@email.com", result.get().getEmail());

        verify(userRepository, times(1)).findByEmail("bogus@email.com");
    }

    @Test
    void getCurrentUser_failure() {

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        Optional<UserDto> result = userService.getCurrentUser();

        assertFalse(result.isPresent());
    }


}

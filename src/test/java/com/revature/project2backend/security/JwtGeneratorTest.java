package com.revature.project2backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import com.revature.project2backend.dto.AuthResponseDto;
import com.revature.project2backend.dto.LoginDto;
import com.revature.project2backend.model.UserEntity;
import com.revature.project2backend.repository.UserRepository;
import com.revature.project2backend.service.EmailSender;
import com.revature.project2backend.service.impl.EmailTokenServiceImpl;
import com.revature.project2backend.service.impl.UserServiceImpl;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class) @SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class JwtGeneratorTest {

    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailTokenServiceImpl emailTokenService;
    @Mock
    EmailSender emailSender;
    @Autowired
    JwtGenerator jwtGenerator;
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

        userService = new UserServiceImpl(authenticationManager,userRepository,passwordEncoder,jwtGenerator, emailTokenService, emailSender);
    }

    @Test
    void login_jwtContainsCorrectInfo() {
        JwtGenerator realJwtGenerator = new JwtGenerator();

        when(userRepository.existsByEmail(loginDto.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        ResponseEntity<AuthResponseDto> response = userService.login(loginDto);

        String jwtToken = realJwtGenerator.generateJwtToken(authentication);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("bogus", response.getBody().getUsername());
        assertEquals("bogus@email.com", response.getBody().getEmail());
        assertEquals(jwtToken, response.getBody().getAccessToken());
    }

    @Test
    void generateJwtToken_correctExpiration() {
        JwtGenerator realJwtGenerator = new JwtGenerator();
        String jwtToken = realJwtGenerator.generateJwtToken(authentication);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(SecurityConstants.JWT_SECRET)
                .build()
                .parseClaimsJws(jwtToken);

        long expirationTime = claims.getBody().getExpiration().getTime() - claims.getBody().getIssuedAt().getTime();
        assertEquals(SecurityConstants.JWT_EXPIRATION, expirationTime, "Expiration time should match JWT_EXPIRATION constant");
    }

    @Test
    void getEmailFromJwt_correctEmail() {
        String jwtToken = jwtGenerator.generateJwtToken(authentication);
        String extractedEmail = jwtGenerator.getEmailFromJwt(jwtToken);
        assertEquals(userEntity.getEmail(), extractedEmail, "Extracted email should match the user's email");
    }

    @Test
    void validateToken_validToken() {
        String jwtToken = jwtGenerator.generateJwtToken(authentication);
        assertDoesNotThrow(() -> jwtGenerator.validateToken(jwtToken), "Valid token should not throw an exception");
    }

    @Test
    void validateToken_invalidSignature() {
        JwtGenerator realJwtGenerator = new JwtGenerator();

        String jwtToken = realJwtGenerator.generateJwtToken(authentication);
        String tamperedToken = jwtToken + "a"; // Append an extra character to the token to make it invalid
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> jwtGenerator.validateToken(tamperedToken), "Invalid token should throw an exception");
    }
}

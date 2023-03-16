// package com.revature.project2backend.service.impl;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.verify;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import com.revature.project2backend.dto.AuthResponseDto;
// import com.revature.project2backend.dto.LoginDto;
// import com.revature.project2backend.dto.RegisterDto;
// import com.revature.project2backend.model.UserEntity;
// import com.revature.project2backend.repository.UserRepository;
// import com.revature.project2backend.security.JwtGenerator;

// public class UserServiceImplTest {
//     AuthenticationManager authenticationManagerMock = Mockito.mock(AuthenticationManager.class);
//     UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
//     PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
//     JwtGenerator jwtGeneratorMock = Mockito.mock(JwtGenerator.class);
    
//     UserServiceImpl userServiceImpl = new UserServiceImpl(authenticationManagerMock, userRepositoryMock, passwordEncoderMock, jwtGeneratorMock);
    
//     LoginDto loginDto;
//     RegisterDto registerDto;

//     @BeforeEach
//     void setUp() {
//         loginDto = new LoginDto();
//         loginDto.setEmail("bogus@email.com");
//         loginDto.setPassword("crazyPassword1!");
        
//         registerDto = new RegisterDto();
//         registerDto.setEmail("bogus@email.com");
//         registerDto.setPassword("crazyPassword1!");
//         registerDto.setUsername("crazyBogus123");
        
//     }

//     // Login Testing

//     @Test
//     void loginShouldReturnAuthDTO() {
//         assertEquals(AuthResponseDto.class, userServiceImpl.login(loginDto).getBody().getClass());
//     }

//     @Test
//     void loginShouldReturnOKIfValid() {
//         assertEquals(HttpStatus.OK, userServiceImpl.login(loginDto).getStatusCode());
//     }

//     // @Test
//     // void loginShouldFailOnEmptyLoginDTO() {
//     //     LoginDto emptyDto = new LoginDto();
//     //     assertNotEquals(HttpStatus.OK, userServiceImpl.login(emptyDto).getStatusCode(), "Expected not OK, but was OK");
//     // }

//     @Test
//     void loginShouldRejectInvalidCredentials() {
//         Mockito.when(authenticationManagerMock.authenticate(any())).thenThrow(BadCredentialsException.class);

//         ResponseEntity<?> response = userServiceImpl.login(loginDto);
//         HttpStatus responseStatus = response.getStatusCode();

//         assertEquals(HttpStatus.BAD_REQUEST, responseStatus);
//         verify(authenticationManagerMock).authenticate(any());
//     }

//     // Register Testing

//     @Test
//     void registerShouldFailIfEmailExists() {
//         Mockito.when(userRepositoryMock.existsByEmail(anyString())).thenReturn(true);

//         assertNotEquals(HttpStatus.OK, userServiceImpl.register(registerDto).getStatusCode());
//         verify(userRepositoryMock).existsByEmail(anyString());
//     }

//     @Test
//     void registerShouldFailIfUserExists() {
//         Mockito.when(userRepositoryMock.existsByUsername(anyString())).thenReturn(true);
        
//         assertNotEquals(HttpStatus.OK, userServiceImpl.register(registerDto).getStatusCode());
//         verify(userRepositoryMock).existsByUsername(anyString());
//     }

//     // @Test
//     // void registerShouldFailIfEmptyDto() {
//     //     RegisterDto emptyDto = new RegisterDto();
//     //     assertNotEquals(HttpStatus.OK, userServiceImpl.register(emptyDto).getStatusCode(), "Expected not OK, but was OK");
//     // }

//     @Test
//     void registerShouldHashPassword() {
//         userServiceImpl.register(registerDto);
        
//         // Checks to make sure password encoder is called
//         verify(passwordEncoderMock).encode(anyString());
//     }

//     @Test
//     void registerShouldSaveUser() {
//         userServiceImpl.register(registerDto);
//         verify(userRepositoryMock).save(any(UserEntity.class));
//     }

//     @Test
//     void registerShouldReturnOKIfSuccessful() {
//         assertEquals(HttpStatus.OK, userServiceImpl.register(registerDto).getStatusCode());
//     }
// }
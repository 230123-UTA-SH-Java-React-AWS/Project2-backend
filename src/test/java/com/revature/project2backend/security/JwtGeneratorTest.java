// package com.revature.project2backend.security;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;

// import java.util.Date;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.security.core.Authentication;

// import com.fasterxml.jackson.datatype.jsr310.deser.key.MonthDayKeyDeserializer;

// import io.jsonwebtoken.JwtBuilder;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;

// public class JwtGeneratorTest {
//     Authentication authenticationMock = Mockito.mock(Authentication.class);
//     String dummyJWT;
//     JwtGenerator jwtGenerator;

//     @BeforeEach
//     void setUp() {
//         jwtGenerator = new JwtGenerator();

//         Mockito.when(authenticationMock.getName()).thenReturn("bogus@email.com");
//         dummyJWT = jwtGenerator.generateJwtToken(authenticationMock);
//     }

//     @Test
//     void testGetEmailFromJwt() {
//         assertEquals("bogus@email.com", jwtGenerator.getEmailFromJwt(dummyJWT));
//     }

//     @Test
//     void validateTokenShouldFailEmptyToken() {
//         String emptyJWT = "";
//         assertThrows(
//             Exception.class, 
//             () -> jwtGenerator.validateToken(emptyJWT),
//             "Expected validate(emptyJWT) to throw Exception, but it didn't"
//         );
//     }

//     @Test
//     void validateTokenShouldFailExpiredToken() {
//         String expiredJWT = Jwts.builder().setSubject("expired@email.com").setIssuedAt(new Date()).setExpiration(new Date()).signWith(SignatureAlgorithm.HS256, SecurityConstants.JWT_SECRET).compact();

//         assertThrows(
//             Exception.class,
//             () -> jwtGenerator.validateToken(expiredJWT),
//             "Expected validate(expiredJWT) to throw Exception, but it didn't"
//         );
//     }

//     // @Test
//     // void validateShouldFailTokenWhenWrongUser() {
//     //     Mockito.when(authenticationMock.getName()).thenReturn("wrong@email.com");
//     //     String anotherUsersJWT = jwtGenerator.generateJwtToken(authenticationMock);

//     //     assertThrows(
//     //         Exception.class,
//     //         () -> jwtGenerator.validateToken(anotherUsersJWT),
//     //         "Expected validate(anotherUsersJWT) to throw Exception, but it didn't"
//     //     );
//     // }
// }
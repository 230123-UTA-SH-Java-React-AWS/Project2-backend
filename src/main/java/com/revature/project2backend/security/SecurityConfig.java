package com.revature.project2backend.security;


import com.sun.tools.javac.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthEntryPoint authEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // setup security rules here
        // CSRF protection disabled TODO: enable it later
        // auth entry point setup with jwt
        // session setup to be stateless meaning: server doesn't maintain any session data and all session data is stored on the client side, so each request from the client must contain necessary info in this case its the jwt token
        //  TODO: "/api/hello" does not exist, this is an example of a GET endpoint that requires users to be authenticated [ .authorizeRequests().antMatchers("/api/hello").authenticated() ] must be between [.and()] methods
        // "/api/auth/**" contains two endpoints "login" and "register", any endpoint after /auth does not require users to be authenticated
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint).and().cors(Customizer.withDefaults()).sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().antMatchers("/api/hello").authenticated().and().authorizeRequests().antMatchers("/api/auth/**").permitAll().anyRequest().authenticated().and().httpBasic();
        // jwt filter will validate tokens on each request
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        // Spring security manager servant bean that authenticates user credentials
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration=  new CorsConfiguration();
        //TODO: change allowed origin to hosted url later
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        //TODO: update list if needed
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //cors on every endpoint, change if needed
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

package com.revature.project2backend.security;


import com.revature.project2backend.exception.CsrfAccessDeniedHandler;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final CustomUserDetailsService userDetailsService;
    private final CsrfAccessDeniedHandler csrfAccessDeniedHandler;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthEntryPoint authEntryPoint, CsrfAccessDeniedHandler csrfAccessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.csrfAccessDeniedHandler = csrfAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // setup security rules here
        // auth entry point setup with jwt
        // session setup to be stateless meaning: server doesn't maintain any session data and all session data is stored on the client side, so each request from the client must contain necessary info in this case its the jwt token
        // "/api/auth/**" contains two endpoints "login" and "register", any endpoint after /auth does not require users to be authenticated
        http.csrf().disable()
                .cors(Customizer.withDefaults())
                .exceptionHandling().authenticationEntryPoint(authEntryPoint).accessDeniedHandler(csrfAccessDeniedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests().antMatchers("/api/csrf").permitAll().and()
                .authorizeHttpRequests().antMatchers("/ws").permitAll().and()
                .authorizeHttpRequests().antMatchers("/api/auth/**").permitAll().anyRequest().authenticated().and().httpBasic();
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
        configuration.setAllowedOrigins(Arrays.asList("http://host.docker.internal:80","http://host.docker.internal:3000","http://host.docker.internal:8000","http://host.docker.internal:4798",
            "http://localhost:80", "http://localhost:3000", "http://localhost:8080", "http://localhost:4798",
            "http://20.185.146.14:80", "http://20.185.146.14:3000", "http://20.185.146.14:8080", "http://20.185.146.14:4798"));
        //TODO: update list if needed
        configuration.setAllowedMethods(Arrays.asList("*"));
        //configuration.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, "X-XSRF-TOKEN"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //cors on every endpoint, change if needed
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        // csrf token can only be accessed using http requests and not javascript
        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
        repository.setCookieHttpOnly(false);
        repository.setSecure(true);
        return repository;
    }
}

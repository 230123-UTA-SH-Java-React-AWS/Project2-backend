package com.revature.project2backend.security;



import com.revature.project2backend.model.UserEntity;
import com.revature.project2backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    private final Set<GrantedAuthority> authorities = new HashSet<>(Collections.singleton(new SimpleGrantedAuthority("USER")));

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email address not found"));
        return new User(user.getEmail(),user.getPassword(), getAuthorities());
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}

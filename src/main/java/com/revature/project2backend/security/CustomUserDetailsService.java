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

    private final UserRepository userRepository;

    // every user will have the "USER" role
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

/*
* CustomUserDetailsService Class Documentation
This class serves as a custom implementation of the UserDetailsService interface, providing a way to load user-specific data from the database. It is responsible for retrieving user data by email address and transforming it into a UserDetails object for Spring Security to use.

Dependencies
UserRepository: A Spring Data JPA repository used to query the UserEntity data from the database.
Methods
CustomUserDetailsService(UserRepository userRepository)
This is the constructor for the CustomUserDetailsService class. It takes a UserRepository object as an argument and assigns it to the userRepository field.

loadUserByUsername(String email)
This method retrieves a user from the database by email address and transforms it into a UserDetails object. If the email address is not found, it throws a UsernameNotFoundException.

getAuthorities()
This method returns a collection of GrantedAuthority objects representing the authorities granted to the user. In this implementation, every user is granted the "USER" role.

Annotations
@Service: Indicates that the class should be treated as a Spring service and managed by the Spring container.
Implements
UserDetailsService: An interface for retrieving user-related data and providing it to Spring Security as a UserDetails object.
* */
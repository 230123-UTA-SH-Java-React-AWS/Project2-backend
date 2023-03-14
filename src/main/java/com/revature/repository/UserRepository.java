package com.revature.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
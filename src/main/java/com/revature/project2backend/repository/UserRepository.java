package com.revature.project2backend.repository;
import com.revature.project2backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

package com.revature.project2backend.repository;
import com.revature.project2backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<UserEntity> findByEmailAndPassword(String email, String password);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u " +
            "SET u.enabled = TRUE WHERE u.email = ?1")
    int enableUser(String email);
}

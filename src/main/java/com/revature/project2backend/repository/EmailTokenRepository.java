package com.revature.project2backend.repository;

import com.revature.project2backend.model.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {

    Optional<EmailToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE EmailToken t " +
            "SET t.confirmedAt = ?2 " +
            "WHERE t.token = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
}

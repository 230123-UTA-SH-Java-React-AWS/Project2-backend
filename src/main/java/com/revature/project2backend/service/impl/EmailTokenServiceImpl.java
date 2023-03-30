package com.revature.project2backend.service.impl;

import com.revature.project2backend.model.EmailToken;
import com.revature.project2backend.repository.EmailTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailTokenServiceImpl {

    private final EmailTokenRepository emailTokenRepository;

    @Autowired
    public EmailTokenServiceImpl(EmailTokenRepository emailTokenRepository) {
        this.emailTokenRepository = emailTokenRepository;
    }

    public void saveEmailToken(EmailToken token){
        emailTokenRepository.save(token);
    }

    public Optional<EmailToken> getToken(String token) {
        return emailTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return emailTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

}

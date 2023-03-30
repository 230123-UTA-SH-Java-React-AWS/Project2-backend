package com.revature.project2backend.service.impl;

import com.revature.project2backend.exception.EmailConfirmationException;
import com.revature.project2backend.service.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service @AllArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender mailSender;

    @Override @Async
    public void send(String to, String email) {

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your blackjack email");
            helper.setFrom("noreply.project.blackjack@gmail.com");
            mailSender.send(mimeMessage);
        }catch(MessagingException e){
            throw new EmailConfirmationException("Failed to send confirmation email");
        }
    }
}

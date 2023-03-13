package com.revature.project2backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.revature.project2backend.entity.Card52;
import com.revature.project2backend.entity.Hand52;

@Controller
public class DemoController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/secured/room")
    public void stand(@Payload List<Card52> cards, Principal user, @Header("simpSessionId") String sessionId)
            throws Exception {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/specific-user",
                new Hand52(cards));
        System.out.println("This user: " + sessionId);
    }

}

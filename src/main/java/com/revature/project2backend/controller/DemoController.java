package com.revature.project2backend.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import com.revature.project2backend.entity.Card52;
import com.revature.project2backend.entity.Deck52;
import com.revature.project2backend.entity.Hand52;

@Controller
public class DemoController {

    @MessageMapping("/hit")
    @SendTo("/gamestate")
    public Hand52 hit(List<Card52> cards) throws Exception {
        Deck52 deck = new Deck52();
        deck.shuffle();
        cards.add(deck.deal());
        return new Hand52(cards);
    }

    @MessageMapping("/stand")
    @SendTo("/gamestate")
    public Hand52 stand(@Payload List<Card52> cards, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        System.out.println();
        headerAccessor.setSessionId(sessionId);
        return new Hand52(cards);
    }

}

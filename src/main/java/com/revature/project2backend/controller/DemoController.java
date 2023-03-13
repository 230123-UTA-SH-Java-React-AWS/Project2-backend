package com.revature.project2backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.revature.CardLogic.Card52;
import com.revature.CardLogic.Deck52;
import com.revature.CardLogic.Hand52;
import com.revature.GameLogic.AllGames.GameRegistry;
import com.revature.GameLogic.Blackjack.BlackjackGame;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class DemoController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/hit")
    @SendTo("/gamestate")
    public Hand52 hit(List<Card52> cards) throws Exception {
        Deck52 deck = new Deck52();
        deck.shuffle();
        cards.add(deck.deal());
        return new Hand52(cards);
    }

    @MessageMapping("/secured/room")
    @SendToUser("/secured/player/queue/specific-player")
    public Hand52 stand(@Payload List<Card52> cards, 
     player, @Header("simpSessionId") String sessionId) throws Exception {
        System.out.println("Specific player: " + sessionId);
        return new Hand52(cards);
    }

    //TODO: remove this function
    @MessageMapping("revealSessionId")
    @SendTo("/gamestate")
    public void revealSessionId(SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        System.out.println(sessionId);
    }

    @MessageMapping("/NewBlackjackGame")
    @SendTo("/gamestate")
    public String newBlackjackGame(SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();

        GameRegistry g = GameRegistry.getGameRegistry();
        g.getRunningGames().add(new BlackjackGame());
        return "test";
    }

    @MessageMapping("/ConnectToBlackjackGame")
    @SendTo("/gamestate")
    public String connectToBlackjackGame(SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();

        GameRegistry g = GameRegistry.getGameRegistry();
        BlackjackGame bj;
        //Iterate through the list of game until we find a game whose initialSessionId matches what was passed in
        
        return "test";
    }
}

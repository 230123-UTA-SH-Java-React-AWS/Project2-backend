package com.revature.project2backend.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.revature.project2backend.model.Card52;
import com.revature.project2backend.model.Deck52;
import com.revature.project2backend.model.Hand52;

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
    public Hand52 stand(List<Card52> cards) throws Exception {
        System.out.println(cards.get(0).getRank());
        System.out.println(cards.get(0).getSuit());
        return new Hand52(cards);
    }

}

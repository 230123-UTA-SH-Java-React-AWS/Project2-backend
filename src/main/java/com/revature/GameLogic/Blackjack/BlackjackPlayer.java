package com.revature.GameLogic.Blackjack;

import com.revature.CardLogic.Card52;

import com.revature.GameLogic.AllGames.Player;

public class BlackjackPlayer extends Player {
    private BlackjackHand hand = new BlackjackHand();

    public void push(Card52 card){
        hand.push(card);
    }

    @Override
    public void sendState() {
        // TODO Auto-generated method stub
        
    }
}

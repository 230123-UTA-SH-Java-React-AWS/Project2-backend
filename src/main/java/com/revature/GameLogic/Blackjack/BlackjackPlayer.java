package com.revature.GameLogic.Blackjack;

import java.util.List;

import com.revature.CardLogic.Card52;

import com.revature.GameLogic.AllGames.Player;

public class BlackjackPlayer extends Player {
    private BlackjackHand hand = new BlackjackHand();
    private boolean hasTakenTurn = false;

    public void push(Card52 card){
        hand.push(card);
    }

    public void push(List<Card52> cards){
        hand.push(cards);
    }

    public void setHasTakenTurn(boolean hasTakenTurn){
        this.hasTakenTurn = hasTakenTurn;
    }

    public boolean getHasTakenTurn() { return hasTakenTurn; }

    @Override
    public void sendState() {
        // TODO Auto-generated method stub
        
    }
}

package com.revature.GameLogic.Blackjack;

import java.util.List;
import java.util.ArrayList;

import com.revature.CardLogic.Deck52;

import com.revature.GameLogic.AllGames.BaseGame;

public class BlackjackGame extends BaseGame {
    Deck52 deck;
    //The dealer always exists and their cards are what gets compared against the players' cards.
    BlackjackPlayer dealer = new BlackjackPlayer();
    List<BlackjackPlayer> players = new ArrayList<>();

    public BlackjackGame() {
        super();
    }

    public void dealHands(){
        deck = new Deck52().shuffle();
        dealer.push(deck.deal(2));
        for(BlackjackPlayer p : players){
            p.push(deck.deal(2));
        }
    }

    @Override
    protected void startGame() {
        //super.startGame();
        dealHands();
    }

    public void onGameStateChange(){
        //Send the new game state to all connected clients
        for(BlackjackPlayer p : players){
            p.sendState();
        }
    }

    public void onPlayerTakesTurn() {
        for (BlackjackPlayer blackjackPlayer : players) {
            if(!blackjackPlayer.getHasTakenTurn()) return;
        }
        //Dealer can do stuff
        //And then this happens
        for (BlackjackPlayer blackjackPlayer : players) {
            blackjackPlayer.setHasTakenTurn(false);
        }
    }

    public void onPlayerHit(BlackjackPlayer p){
        //Deal a card
        onGameStateChange();
    }
}

package com.revature.GameLogic.Blackjack;

import java.util.List;
import java.util.Collections;

import com.revature.CardLogic.Deck52;
import com.revature.CardLogic.Hand52;

import com.revature.GameLogic.AllGames.BaseGame;

public class BlackjackGame extends BaseGame {
    //Blackjack is played, in our case, with eight decks shuffled together.
    //This more or less eliminates card counting.
    Hand52 bigHand;
    //The dealer always exists and their cards are what gets compared against the players' cards.
    BlackjackPlayer dealer = new BlackjackPlayer();
    List<BlackjackPlayer> players;

    public BlackjackGame() {
        super();
        bigHand = new Hand52();
        for(int i = 0; i < 8; i++) {
            bigHand.push(new Deck52().deal(52));
        }
        Collections.shuffle(bigHand.getCards());
    }

    public void onGameStateChange(){
        //Send the new game state to all connected clients
        for(BlackjackPlayer p : players){
            p.sendState();
        }
    }

    public void onPlayerHit(BlackjackPlayer p){
        //Deal a card
        onGameStateChange();
    }
}

package com.revature.GameLogic.Blackjack;

import java.util.List;

import com.revature.CardLogic.Card52;

import com.revature.GameLogic.AllGames.BasePlayer;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class BlackjackPlayer extends BasePlayer<BlackjackClientGameState> {
    public enum EndGameStates {STILL_PLAYING, IS_BUSTED, DEALER_BUSTED, BLACKJACK, LOST_TO_DEALER, TIED_DEALER, BEAT_DEALER}
    @Getter
    private BlackjackHand hand = new BlackjackHand();
    @Getter @Setter
    private boolean isTurnEnded = false;
    @Getter @Setter @NonNull
    private EndGameStates endGameState = EndGameStates.STILL_PLAYING;

    public BlackjackPlayer(){
        super();
    }

    public void push(Card52 card){
        hand.push(card);
    }

    public void push(List<Card52> cards){
        hand.push(cards);
    }
}

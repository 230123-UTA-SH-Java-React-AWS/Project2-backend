package com.revature.GameLogic.Blackjack;

import java.util.List;

import com.revature.CardLogic.Card52;

import com.revature.GameLogic.AllGames.BasePlayer;

public class BlackjackPlayer extends BasePlayer<BlackjackClientGameState> {
    public enum EndGameStates {STILL_PLAYING, IS_BUSTED, DEALER_BUSTED, BLACKJACK, LOST_TO_DEALER, TIED_DEALER, BEAT_DEALER}
    private BlackjackHand hand = new BlackjackHand();
    private boolean hasEndedTurn = false;
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

    public void setHasEndedTurn(boolean hasEndedTurn){
        this.hasEndedTurn = hasEndedTurn;
    }

    public boolean getHasEndedTurn() {
        return hasEndedTurn;
    }

    public BlackjackHand getHand() {
        return hand;
    }

    public EndGameStates getEndGameState() {
        return endGameState;
    }

    public void setEndGameState(EndGameStates endGameState) {
        if(endGameState != null) {
            this.endGameState = endGameState;
        }
    }

    @Override
    public void sendState() {
        simpMessagingTemplate.convertAndSendToUser(urlSuffix, "", clientGameState);
    }

    @Override
    public void sendWaitingData(int pos, int total){
        //unimplemented at this time because I still need to figure out websockets.
    }

    @Override
    public void onMessageReceived(){
        //unimplemented at this time because I need to determine if handling messages
        // that the player sends in Player is the right move or not.
    }
}

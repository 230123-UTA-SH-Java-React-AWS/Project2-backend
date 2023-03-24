package com.revature.game_logic.blackjack;

import java.util.List;

import com.revature.card_logic.Card52;
import com.revature.game_logic.common.BasePlayer;

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
    @Getter @Setter
    private boolean isDoubledDown = false;

    public BlackjackPlayer(String playerName){
        super(playerName);
    }

    public void push(Card52 card){
        hand.push(card);
    }

    public void push(List<Card52> cards){
        hand.push(cards);
    }
}

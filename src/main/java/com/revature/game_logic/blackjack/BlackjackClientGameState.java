package com.revature.game_logic.blackjack;

import java.util.List;

import com.revature.card_logic.Card52;
import com.revature.game_logic.blackjack.BlackjackPlayer.EndGameStates;
import com.revature.game_logic.common.BaseClientGameState;

import lombok.AllArgsConstructor;
import lombok.Data;


public @Data @AllArgsConstructor class BlackjackClientGameState implements BaseClientGameState {
    private List<Card52> dealersCards;
    private List<BlackjackPlayerInfo> players;

    public static @Data @AllArgsConstructor class BlackjackPlayerInfo {
        EndGameStates endGameState;
        String playerName;
        boolean hasTakenTurn;
        List<Card52> cards;
        boolean isHost;
    }
}

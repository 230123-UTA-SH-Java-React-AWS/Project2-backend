package com.revature.GameLogic.Blackjack;

import java.util.List;

import com.revature.CardLogic.Card52;
import com.revature.GameLogic.AllGames.BaseClientGameState;
import com.revature.GameLogic.Blackjack.BlackjackPlayer.EndGameStates;

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
    }
}

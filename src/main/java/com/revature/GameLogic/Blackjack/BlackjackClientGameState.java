package com.revature.GameLogic.Blackjack;

import java.util.List;

import com.revature.CardLogic.Card52;
import com.revature.GameLogic.AllGames.BaseClientGameState;
import com.revature.GameLogic.Blackjack.BlackjackPlayer.EndGameStates;

public class BlackjackClientGameState implements BaseClientGameState {
    private List<Card52> dealersCards;
    private List<BlackjackPlayerInfo> players;

    public BlackjackClientGameState(List<Card52> dealersCards, List<BlackjackPlayerInfo> players){
        this.dealersCards = dealersCards;
        this.players = players;
    }

    public static class BlackjackPlayerInfo {
        String playerName;
        boolean hasTakenTurn;
        List<Card52> cards;
        EndGameStates endGameState;

        public BlackjackPlayerInfo(EndGameStates endGameState, String playerName, boolean hasTakenTurn, List<Card52> cards) {
            this.endGameState = endGameState;
            this.playerName = playerName;
            this.hasTakenTurn = hasTakenTurn;
            this.cards = cards;
        }
    }
}

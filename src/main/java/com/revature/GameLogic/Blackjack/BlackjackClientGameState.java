package com.revature.GameLogic.Blackjack;

import java.util.List;

import com.revature.CardLogic.Card52;
import com.revature.GameLogic.AllGames.BaseClientGameState;

public class BlackjackClientGameState extends BaseClientGameState {
    private List<Card52> dealersCards;
    private List<BlackjackPlayerInfo> players;

    public static class BlackjackPlayerInfo {
        String profilePictureLink;
        String playerName;
        boolean hasTakenTurn;
        List<Card52> cards;

        public BlackjackPlayerInfo(String profilePictureLink, String playerName, boolean hasTakenTurn, List<Card52> cards) {
            this.profilePictureLink = profilePictureLink;
            this.playerName = playerName;
            this.hasTakenTurn = hasTakenTurn;
            this.cards = cards;
        }
    }
}

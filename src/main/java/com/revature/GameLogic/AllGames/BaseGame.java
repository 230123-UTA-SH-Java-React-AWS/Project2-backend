package com.revature.GameLogic.AllGames;

public abstract class BaseGame {
    //Used to determine if the game has started yet.
    //A game that has started should no longer accept more players, and a game which has not started should not run game logic.
    //A game should also never go from started to not started.
    protected boolean isGameStarted = false;

    protected void startGame() {
        isGameStarted = true;
    }

    //Websocket connections go here if they're common to all games

}

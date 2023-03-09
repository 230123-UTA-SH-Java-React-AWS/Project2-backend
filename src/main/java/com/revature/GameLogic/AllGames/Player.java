package com.revature.GameLogic.AllGames;

//Represents a player who has successfully connected to a game. Each player should represent one person at the table,
// with one connection established. Implementation left empty below pending discussion with team.
public abstract class Player {
    protected BaseGame game;
    protected BaseClientGameState clientGameState;

    public abstract void sendState();

    public void onReceiveMessage(/* any info required to update the game state */){
        game.updatePlayers();
    }
}

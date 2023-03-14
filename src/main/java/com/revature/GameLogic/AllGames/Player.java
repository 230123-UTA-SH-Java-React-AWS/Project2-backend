package com.revature.GameLogic.AllGames;

import com.revature.project2backend.controller.GameController;
import org.springframework.beans.factory.annotation.Autowired;

//Represents a player who has successfully connected to a game. Each player should represent one person at the table,
// with one connection established. Implementation left empty below pending discussion with team.
public abstract class Player<T extends BaseClientGameState> {
    @Autowired
    protected GameController gameController;
    protected String urlSuffix;
    protected T clientGameState; //The game state that this client has.

    protected Player(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    //Send the current game state to the client.
    //It may be possible to implement this here so that it never needs to be overridden.
    public abstract void sendState();
    //This should be implemented here.
    //This should send data to the user so that they can display something like "Waiting for a free seat at the table, you are #2 / 6 in the queue."
    public abstract void sendWaitingData(int positionInQueue, int numWaitingPlayers);

    public String getUrlSuffix() { return urlSuffix; }

    public void setClientGameState(T clientGameState) {
        this.clientGameState = clientGameState;
        sendState();
    }

    public abstract void onMessageReceived();
}

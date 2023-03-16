package com.revature.GameLogic.AllGames;

import java.security.SecureRandom;

import com.revature.project2backend.controller.GameController;

import lombok.Getter;
import lombok.Setter;

//Represents a player who has successfully connected to a game. Each player should represent one person at the table,
// with one connection established. Implementation left empty below pending discussion with team.
public abstract class BasePlayer<T extends BaseClientGameState> {
    private static final String URL_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";

    @Getter
    protected String playerId;
    @Setter
    protected T clientGameState; //The game state that this client has.
    protected GameController gameController;

    protected BasePlayer(GameController gameController) {
        this.gameController = gameController;
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 64; i++){
            int pos = rand.nextInt() % URL_CHARS.length();
            if(pos < 0) pos *= -1;
            sb.append(URL_CHARS.charAt(pos));
        }
        playerId = sb.toString();
    }

    //Send the current game state to the client.
    //It may be possible to implement this here so that it never needs to be overridden.
    public abstract void sendState();
    //This should be implemented here.
    //This should send data to the user so that they can display something like "Waiting for a free seat at the table, you are #2 / 6 in the queue."
    public void sendWaitingData(int positionInQueue, int numWaitingPlayers){
        gameController.sendQueueState(playerId, positionInQueue, numWaitingPlayers);
    }

    public abstract void onMessageReceived();
}

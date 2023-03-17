package com.revature.GameLogic.AllGames;

import com.revature.project2backend.controller.GameController;

import lombok.Getter;
import lombok.Setter;

//Represents a player who has successfully connected to a game. Each player should represent one person at the table,
// with one connection established. Implementation left empty below pending discussion with team.
public abstract class BasePlayer<T extends BaseClientGameState> {
    @Getter
    protected final String playerId = IdGenerator.generate_id(); // A unique identifier for this player
    @Getter
    @Setter
    protected T clientGameState; // The game state that this client has.
    protected GameController gameController; // A reference to the GameController so we can update the socket

    protected BasePlayer(GameController gameController) {
        this.gameController = gameController;
    }

    // Send the current game state to the client.
    // It may be possible to implement this here so that it never needs to be
    // overridden.
    public abstract void sendState();

    // This should be implemented here.
    // This should send data to the user so that they can display something like
    // "Waiting for a free seat at the table, you are #2 / 6 in the queue."
    public void sendWaitingData(int positionInQueue, int numWaitingPlayers) {
        gameController.sendQueueState(playerId, positionInQueue, numWaitingPlayers);
    }

    public abstract void onMessageReceived();
}

package com.revature.GameLogic.AllGames;

//Represents a player who has successfully connected to a game. Each player should represent one person at the table,
// with one connection established. Implementation left empty below pending discussion with team.
public abstract class Player {
    protected BaseClientGameState clientGameState; //The game state that this client has.

    //Send the current game state to the client.
    //It may be possible to implement this here so that it never needs to be overridden.
    public abstract void sendState();
    //This should be implemented here.
    //This should send data to the user so that they can display something like "Waiting for a free seat at the table, you are #2 / 6 in the queue."
    public abstract void sendWaitingData(int positionInQueue, int numWaitingPlayers);

    public void setClientGameState(BaseClientGameState clientGameState) {
        this.clientGameState = clientGameState;
        sendState();
    }

    public abstract void onMessageReceived();
}

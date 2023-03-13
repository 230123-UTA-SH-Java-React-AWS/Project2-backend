package com.revature.GameLogic.AllGames;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseGame<T extends Player> {
    protected Queue<T> waitingPlayers = new ConcurrentLinkedQueue<>(); 
    protected List<T> activePlayers = new ArrayList<>();

    protected final int maxActivePlayers;

    protected BaseGame(int maxActivePlayers){
        this.maxActivePlayers = maxActivePlayers;
    }

    //Bring players from the waiting queue into the actual game.
    protected void admitPlayers(){
        while(activePlayers.size() < maxActivePlayers && !waitingPlayers.isEmpty()) {
            activePlayers.add(waitingPlayers.remove());
        }
        updateWaitingPlayers();
    }

    public void updateWaitingPlayers(){
        int i = 1;
        for(T player : waitingPlayers){
            player.sendWaitingData(i, waitingPlayers.size());
            i++;
        }
    }

    public void updateActivePlayers(){
        for(T player : activePlayers){
            player.sendState();
        }
    }
}

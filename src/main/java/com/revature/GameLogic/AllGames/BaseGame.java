package com.revature.GameLogic.AllGames;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseGame<T extends Player> {
    private static final String URL_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
    protected String urlSuffix;

    protected Queue<T> waitingPlayers = new ConcurrentLinkedQueue<>(); 
    protected List<T> activePlayers = new ArrayList<>();

    protected final int maxActivePlayers;

    protected BaseGame(int maxActivePlayers){
        this.maxActivePlayers = maxActivePlayers;
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 64; i++){
            int pos = rand.nextInt() % URL_CHARS.length();
            sb.append(URL_CHARS.charAt(pos));
        }
        urlSuffix = sb.toString();
    }

    public String getUrlSuffix() { return urlSuffix; }

    //Bring players from the waiting queue into the actual game.
    protected void admitPlayers(){
        while(activePlayers.size() < maxActivePlayers && !waitingPlayers.isEmpty()) {
            activePlayers.add(waitingPlayers.remove());
        }
        updateWaitingPlayers();
    }

    public void addPlayer(T player){
        waitingPlayers.add(player);
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

package com.revature.GameLogic.AllGames;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseGame<T extends BasePlayer<?>> {
    public enum GameType {BLACKJACK}
    private static final String URL_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";

    protected String urlSuffix;
    protected GameType gameType = null;

    protected String gameName;
    protected boolean isPrivateGame;

    protected Queue<T> waitingPlayers = new ConcurrentLinkedQueue<>();
    protected List<T> activePlayers = new ArrayList<>();

    protected final int maxActivePlayers;

    protected BaseGame(String gameName, boolean isPrivateGame, int maxActivePlayers){
        this.gameName = gameName;
        this.isPrivateGame = isPrivateGame;
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

    public String getGameName(){ return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public GameType getGameType(){ return gameType; }

    public boolean getIsPrivateGame() { return isPrivateGame; }

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

    protected T getActivePlayerByUrlSuffix(String playerId){
        for(T player : activePlayers){
            if(player.getUrlSuffix() == playerId) return player;
        }
        return null;
    }

    public GameRepresentation representation(){
        return new GameRepresentation(gameType, urlSuffix, gameName, activePlayers.size(), maxActivePlayers, waitingPlayers.size());
    }

    public static class GameRepresentation {
        public final GameType gameType;
        public final String urlSuffix;
        public final String gameName;
        public final int numActivePlayers;
        public final int numMaxPlayers;
        public final int numWaitingPlayers;

        protected GameRepresentation(GameType gameType, String urlSuffix, String gameName, int numActivePlayers, int numMaxPlayers, int numWaitingPlayers){
            this.gameType = gameType;
            this.urlSuffix = urlSuffix;
            this.gameName = gameName;
            this.numActivePlayers = numActivePlayers;
            this.numMaxPlayers = numMaxPlayers;
            this.numWaitingPlayers = numWaitingPlayers;
        }
    }
}

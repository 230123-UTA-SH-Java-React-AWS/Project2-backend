package com.revature.GameLogic.AllGames;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseGame<T extends BasePlayer<?>> {
    public enum GameType {BLACKJACK}
    private static final String URL_CHARS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";

    @Getter
    protected String gameId;
    @Getter
    protected GameType gameType = null;
    @Getter @Setter
    protected String gameName;
    @Getter
    protected boolean isPrivateGame;

    protected Queue<T> waitingPlayers = new ConcurrentLinkedQueue<>();
    protected List<T> activePlayers = new ArrayList<>();

    protected final int maxActivePlayers;

    protected BaseGame(String gameName, boolean isPrivateGame, int maxActivePlayers){
        this.gameName = gameName;
        this.isPrivateGame = isPrivateGame;
        this.maxActivePlayers = maxActivePlayers;
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(64);
        for(int i = 0; i < 64; i++){
            int pos = rand.nextInt() % URL_CHARS.length();
            if(pos < 0) pos *= -1;
            sb.append(URL_CHARS.charAt(pos));
        }
        gameId = sb.toString();
    }

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
            if(Objects.equals(player.getPlayerId(), playerId)) return player;
        }
        return null;
    }

    public GameRepresentation representation(){
        return new GameRepresentation(gameType, gameId, gameName, activePlayers.size(), maxActivePlayers, waitingPlayers.size());
    }

    public static @AllArgsConstructor class GameRepresentation {
        public final GameType gameType;
        public final String gameId;
        public final String gameName;
        public final int numActivePlayers;
        public final int numMaxPlayers;
        public final int numWaitingPlayers;
    }
}

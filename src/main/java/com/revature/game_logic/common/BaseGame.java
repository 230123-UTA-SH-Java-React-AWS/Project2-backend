package com.revature.game_logic.common;

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

    @Getter
    protected String gameId = IdGenerator.generate_id(); //A unique identifier for this game
    @Getter
    protected GameType gameType = null; //Displays the game type (ex: Blackjack), used in the lobby display screen
    @Getter @Setter
    protected String gameName; //The name of the game, as defined by a player (ex: "Connor's Blackjack Game")
    @Getter
    protected boolean isPrivateGame; //Used to determine whether or not this game is shown by the listGames endpoint
    @Getter
    protected boolean isGameStarted; //Used to determine if a game is in progress (and thus if more players can be admitted)
    @Getter @Setter
    protected T hostPlayer = null;

    protected Queue<T> waitingPlayers = new ConcurrentLinkedQueue<>();
    protected List<T> activePlayers = new ArrayList<>();

    protected final int maxActivePlayers;

    protected BaseGame(String gameName, boolean isPrivateGame, int maxActivePlayers){
        this.gameName = gameName;
        this.isPrivateGame = isPrivateGame;
        this.maxActivePlayers = maxActivePlayers;
    }

    //Bring players from the waiting queue into the actual game.
    protected void admitPlayers(){
        if(isGameStarted) return; //Cannot allow players in if the game is in progress
        while(activePlayers.size() < maxActivePlayers && !waitingPlayers.isEmpty()) {
            activePlayers.add(waitingPlayers.remove());
        }
        updateWaitingPlayers();
        isGameStarted = true;
    }

    public void addPlayer(T player){
        if(waitingPlayers.isEmpty() && activePlayers.isEmpty()) hostPlayer = player;
        waitingPlayers.add(player);
    }

    public void updateWaitingPlayers(){
        int i = 1;
        for(T player : waitingPlayers){
            player.sendWaitingData(
                i,
                waitingPlayers.size(),
                Objects.equals(hostPlayer.getPlayerId(), player.getPlayerId())
            );
            i++;
        }
    }

    protected T getActivePlayerByUrlSuffix(String playerId){
        for(T player : activePlayers){
            if(Objects.equals(player.getPlayerId(), playerId)) return player;
        }
        return null;
    }

    public boolean isPlayerHost(String playerId){
        if(hostPlayer == null) return false;
        return Objects.equals(playerId, hostPlayer.getPlayerId());
    }

    //This function only removed a waiting player from the queue. Child classes are
    // expected to override this with a super call to this function and then handle removing
    // players from the active list (as this involves game-specific logic).
    //Child classes should call chooseNewHost() after this function call.
    protected void dropPlayer(String playerId){
        T markedForDrop = null;
        for(T player : waitingPlayers){
            if(Objects.equals(player.playerId, playerId)){
                markedForDrop = player;
            }
        }
        if (markedForDrop != null) {
            waitingPlayers.remove();
        }
    }

    //This will change the host player to the first available player, either the first active player
    // or the first person in the waiting players list.
    protected final void chooseNewHost(){
        if(hostPlayer == null){
            if(!activePlayers.isEmpty()){
                hostPlayer = activePlayers.get(0);
            } else {
                hostPlayer = waitingPlayers.peek();
            }
        }
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

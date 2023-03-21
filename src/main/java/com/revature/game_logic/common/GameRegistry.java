package com.revature.game_logic.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.revature.game_logic.common.BaseGame.GameRepresentation;

import lombok.Getter;

public class GameRegistry {
    @Getter
    private static Map<String, BaseGame<?>> runningGames = new HashMap<>();

    private GameRegistry(){}

    public static List<GameRepresentation> getPublicGames() {
        List<GameRepresentation> games = new ArrayList<>();
        for(Map.Entry<String, BaseGame<?>> g : runningGames.entrySet()){
            if(!g.getValue().isPrivateGame()){
                games.add(g.getValue().representation());
            }
        }
        return games;
    }

    public static void addNewGame(BaseGame<?> newGame){
        runningGames.put(newGame.getGameId(), newGame);
    }

    public static void removeGame(String gameId){
        runningGames.remove(gameId);
    }

    //I don't even know what SonarLint is mad about here.
    public static BaseGame<?> getGameByUrlSuffix(String urlSuffix){
        return runningGames.get(urlSuffix);
    }
}

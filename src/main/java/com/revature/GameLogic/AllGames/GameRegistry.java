package com.revature.GameLogic.AllGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.revature.GameLogic.AllGames.BaseGame.GameRepresentation;

import lombok.Getter;

public class GameRegistry {
    private static GameRegistry thisGameRegistry = null;
    @Getter
    private static Map<String, BaseGame<?>> runningGames = new HashMap<>();

    private GameRegistry(){}

    public List<GameRepresentation> getPublicGames() {
        List<GameRepresentation> games = new ArrayList<>();
        for(Map.Entry<String, BaseGame<?>> g : runningGames.entrySet()){
            if(!g.getValue().isPrivateGame()){
                games.add(g.getValue().representation());
            }
        }
        return games;
    }

    public void addNewGame(BaseGame<?> newGame){
        runningGames.put(newGame.getUrlSuffix(), newGame);
    }

    //I don't even know what SonarLint is mad about here.
    public BaseGame<?> getGameByUrlSuffix(String urlSuffix){
        return runningGames.get(urlSuffix);
    }

    public static GameRegistry getGameRegistry(){
        if(thisGameRegistry == null) thisGameRegistry = new GameRegistry();
        return thisGameRegistry;
    }
}

package com.revature.GameLogic.AllGames;

import java.util.HashMap;
import java.util.Map;

public class GameRegistry {
    private static GameRegistry thisGameRegistry = null;
    private static Map<String, BaseGame> runningGames = new HashMap<>();

    private GameRegistry(){}

    public Map<String, BaseGame> getRunningGames() {
        return runningGames;
    }

    public void addNewGame(BaseGame newGame){
        runningGames.put(newGame.getUrlSuffix(), newGame);
    }

    public BaseGame getGameByUrlSuffix(String urlSuffix){
        return runningGames.get(urlSuffix);
    }

    public static GameRegistry getGameRegistry(){
        if(thisGameRegistry == null) thisGameRegistry = new GameRegistry();
        return thisGameRegistry;
    }
}

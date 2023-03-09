package com.revature.GameLogic.AllGames;

import java.util.ArrayList;
import java.util.List;

public class GameRegistry {
    private static GameRegistry thisGameRegistry = null;
    private List<BaseGame> runningGames = new ArrayList<>();

    private GameRegistry(){}

    public List<BaseGame> getRunningGames() {
        return this.runningGames;
    }

    public void setRunningGames(List<BaseGame> runningGames) {
        this.runningGames = runningGames;
    }

    public static GameRegistry getGameRegistry(){
        if(thisGameRegistry == null) thisGameRegistry = new GameRegistry();
        return thisGameRegistry;
    }
}

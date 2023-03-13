package com.revature.GameLogic.AllGames;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGame {
    //Used to determine if the game has started yet.
    //A game that has started should no longer accept more players, and a game which has not started should not run game logic.
    //A game should also never go from started to not started.

    protected List<Player> players = new ArrayList<>();

    public BaseGame(){

    }

    protected abstract void startGame();

    public void updatePlayers(){
        for(Player p : players){
            p.sendState();
        }
    }

    //Websocket connections go here if they're common to all games

}

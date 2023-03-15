package com.revature.project2backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.revature.GameLogic.AllGames.BaseGame;
import com.revature.GameLogic.AllGames.GameRegistry;
import com.revature.GameLogic.AllGames.BaseGame.GameRepresentation;
import com.revature.GameLogic.Blackjack.BlackjackGame;
import com.revature.GameLogic.Blackjack.BlackjackPlayer;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;

@Controller
public class GameController {
    
    @GetMapping("allGames")
    public List<GameRepresentation> getAllGames() {
        return GameRegistry.getGameRegistry().getPublicGames();
    }

    @PostMapping("createblackjackGame")
    public String createBlackjackGame(@RequestHeader String gameName, @RequestHeader boolean lobbyIsPrivate) {
        BaseGame<BlackjackPlayer> newGame = new BlackjackGame(gameName, lobbyIsPrivate);
        GameRegistry.getGameRegistry().addNewGame(newGame);
        return newGame.getUrlSuffix();
    }

    @PutMapping("joinGame")
    public ResponseEntity<String> joinBlackjackGame(@Payload String gameUrl, @RequestHeader String sessionId) {
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameUrl);
        BlackjackGame blackjackGame;
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is using joinBlackjackGame to attempt to join a non-blackjack game
            return ResponseEntity.status(403).body("");
        }
        BlackjackPlayer p = new BlackjackPlayer();
        blackjackGame.addPlayer(p);
        return ResponseEntity.status(200).body(p.getUrlSuffix());
    }

    @PutMapping("blackjackAction")
    public void doBlackjackAction(@Payload String gameUrl, @RequestHeader String sessionId, @RequestHeader String actionVerb){
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameUrl);
        BlackjackGame blackjackGame;
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is trying to do a blackjack action on a non-blackjack game
            return;
        }

        switch(actionVerb){
            case "HIT": //Player wants to take a new card
                blackjackGame.onPlayerHit(sessionId);
                break;
            case "STAND": //Player wants to end their turn without taking any more cards
                blackjackGame.onPlayerStand(sessionId);
                break;
            default: //User is trying to take an invalid action, like "kdlfsaghiufdshgi"
        }
    }
    
}

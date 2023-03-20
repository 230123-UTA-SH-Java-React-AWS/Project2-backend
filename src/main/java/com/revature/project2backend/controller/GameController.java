package com.revature.project2backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.revature.game_logic.blackjack.BlackjackGame;
import com.revature.game_logic.blackjack.BlackjackPlayer;
import com.revature.game_logic.common.BaseGame;
import com.revature.game_logic.common.GameRegistry;
import com.revature.game_logic.common.BaseGame.GameRepresentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@CrossOrigin(originPatterns = "*")
@RestController
public class GameController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @GetMapping("allGames")
    public List<GameRepresentation> getAllGames() {
        return GameRegistry.getGameRegistry().getPublicGames();
    }

    @PostMapping("createBlackjackGame")
    public ResponseEntity<String> createBlackjackGame(@RequestHeader String gameName, @RequestHeader boolean lobbyIsPrivate) {
        try {
            BaseGame<BlackjackPlayer> newGame = new BlackjackGame(gameName, lobbyIsPrivate);
            GameRegistry.getGameRegistry().addNewGame(newGame);
            return ResponseEntity.status(200).body(newGame.getGameId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not open game session.");
        }
    }

    @PutMapping("joinBlackjackGame")
    public ResponseEntity<String> joinBlackjackGame(@RequestHeader String gameId) {
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is using joinBlackjackGame to attempt to join a non-blackjack game
            return ResponseEntity.status(403).body("");
        }
        try{
            BlackjackPlayer p = new BlackjackPlayer();
            p.setSimpMessagingTemplate(simpMessagingTemplate);
            blackjackGame.addPlayer(p);
            return ResponseEntity.status(200).body(p.getPlayerId());
        } catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("startBlackjackGame")
    public ResponseEntity<String> startBlackjackGame(@RequestHeader String gameId){
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if(game == null) return ResponseEntity.status(404).body("That game does not appear to exist!");
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is using joinBlackjackGame to attempt to join a non-blackjack game
            return ResponseEntity.status(403).body("Cannot join that game!");
        }
        blackjackGame.dealHands();
        return ResponseEntity.status(204).body("");
    }

    @PutMapping("blackjackAction")
    public void doBlackjackAction(@RequestHeader String gameId, @RequestHeader String playerId, @RequestHeader String actionVerb){
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is trying to do a blackjack action on a non-blackjack game
            return;
        }

        switch(actionVerb){
            case "HIT": //Player wants to take a new card
                blackjackGame.onPlayerHit(playerId);
                break;
            case "STAND": //Player wants to end their turn without taking any more cards
                blackjackGame.onPlayerStand(playerId);
                break;
            default: //User is trying to take an invalid action, like "kdlfsaghiufdshgi"
        }
    }
}

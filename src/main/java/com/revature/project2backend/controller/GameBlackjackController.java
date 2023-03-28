package com.revature.project2backend.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.revature.game_logic.blackjack.BlackjackGame;
import com.revature.game_logic.blackjack.BlackjackPlayer;
import com.revature.game_logic.common.BaseGame;
import com.revature.game_logic.common.BaseGame.GameRepresentation;
import com.revature.game_logic.common.GameRegistry;

@CrossOrigin(originPatterns = "*")
@RestController
public class GameBlackjackController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("createBlackjackGame")
    public ResponseEntity<String> createBlackjackGame(@RequestHeader String gameName, @RequestHeader boolean lobbyIsPrivate) {
        try {
            BaseGame<BlackjackPlayer> newGame = new BlackjackGame(gameName, lobbyIsPrivate);
            GameRegistry.addNewGame(newGame);
            return ResponseEntity.status(200).body(newGame.getGameId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not open game session.");
        }
    }

    @PutMapping("joinBlackjackGame")
    public ResponseEntity<String> joinBlackjackGame(@RequestHeader String gameId, @RequestHeader String username) {
        BaseGame<?> game = GameRegistry.getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        //Error condition - the user is attempting to join a game that does not exist.
        if(game == null) return ResponseEntity.status(404).body("");
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is using joinBlackjackGame to attempt to join a non-blackjack game
            return ResponseEntity.status(403).body("");
        }
        try{
            BlackjackPlayer p = new BlackjackPlayer(username);
            p.setSimpMessagingTemplate(simpMessagingTemplate);
            blackjackGame.addPlayer(p);
            return ResponseEntity.status(200).body(p.getPlayerId());
        } catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("leaveBlackjackGame")
    public void leaveBlackjackGame(@RequestHeader String gameId, @RequestHeader String playerId){
        BaseGame<?> game = GameRegistry.getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if(game == null) return;
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            return;
        }
        blackjackGame.dropPlayer(playerId);
        GameRepresentation gr = blackjackGame.representation();
        if(gr.numActivePlayers == 0 && gr.numWaitingPlayers == 0){
            GameRegistry.removeGame(gameId);
        }
    }

    @PutMapping("startBlackjackGame")
    public ResponseEntity<String> startBlackjackGame(@RequestHeader String gameId, @RequestHeader String playerId){
        BaseGame<?> game = GameRegistry.getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if(game == null) return ResponseEntity.status(404).body("That game does not appear to exist!");
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is using joinBlackjackGame to attempt to join a non-blackjack game
            return ResponseEntity.status(403).body("Cannot join that game!");
        }
        //Only the host of a game can start the game. To see whether or not you are the host, use the amIHost endpoint
        if(Objects.equals(blackjackGame.getHostPlayer().getPlayerId(), playerId)) {
            blackjackGame.dealHands();
            return ResponseEntity.status(204).body("");
        }
        return ResponseEntity.status(403).body("You are not the host.");
    }

    @PutMapping("blackjackAction")
    public void doBlackjackAction(@RequestHeader String gameId, @RequestHeader String playerId, @RequestHeader String actionVerb){
        BaseGame<?> game = GameRegistry.getGameByUrlSuffix(gameId);
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
            case "DOUBLE_DOWN":
                blackjackGame.onPlayerDoubleDown(playerId);
                break;
            default: //User is trying to take an invalid action, like "kdlfsaghiufdshgi"
        }
    }

    @GetMapping("requestGameState")
    public void requestBlackjackGameState(@RequestHeader String gameId, @RequestHeader String playerId){
        BaseGame<?> game = GameRegistry.getGameByUrlSuffix(gameId);
        if(game instanceof BlackjackGame){
            BlackjackGame blackjackGame = (BlackjackGame)game;
            blackjackGame.sendState(playerId);
        }
    }
}

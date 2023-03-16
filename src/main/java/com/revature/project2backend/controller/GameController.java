package com.revature.project2backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.revature.GameLogic.AllGames.BaseClientGameState;
import com.revature.GameLogic.AllGames.BaseGame;
import com.revature.GameLogic.AllGames.GameRegistry;
import com.revature.GameLogic.AllGames.QueueState;
import com.revature.GameLogic.AllGames.BaseGame.GameRepresentation;
import com.revature.GameLogic.Blackjack.BlackjackGame;
import com.revature.GameLogic.Blackjack.BlackjackPlayer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@CrossOrigin
@RestController
public class GameController {
    @Autowired
    private static SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("allGames")
    public List<GameRepresentation> getAllGames() {
        return GameRegistry.getGameRegistry().getPublicGames();
    }

    @PostMapping("createBlackjackGame")
    public ResponseEntity<String> createBlackjackGame(@RequestHeader String gameName,
            @RequestHeader boolean lobbyIsPrivate) {
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
        if (game instanceof BlackjackGame) {
            blackjackGame = (BlackjackGame) game;
        } else {
            // Error condition - the user is using joinBlackjackGame to attempt to join a
            // non-blackjack game
            return ResponseEntity.status(403).body("");
        }
        try {
            BlackjackPlayer p = new BlackjackPlayer(this);
            blackjackGame.addPlayer(p);
            return ResponseEntity.status(200).body(p.getPlayerId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("startBlackjackGame")
    public void startBlackjackGame(@RequestHeader String gameId) {
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if (game instanceof BlackjackGame) {
            blackjackGame = (BlackjackGame) game;
        } else {
            // Error condition - the user is using joinBlackjackGame to attempt to join a
            // non-blackjack game
            return;
        }
        blackjackGame.dealHands();
    }

    @PutMapping("blackjackAction")
    public void doBlackjackAction(@Payload String gameId, @RequestHeader String playerId,
            @RequestHeader String actionVerb) {
        BaseGame<?> game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameId);
        BlackjackGame blackjackGame;
        if (game instanceof BlackjackGame) {
            blackjackGame = (BlackjackGame) game;
        } else {
            // Error condition - the user is trying to do a blackjack action on a
            // non-blackjack game
            return;
        }

        switch (actionVerb) {
            case "HIT": // Player wants to take a new card
                blackjackGame.onPlayerHit(playerId);
                break;
            case "STAND": // Player wants to end their turn without taking any more cards
                blackjackGame.onPlayerStand(playerId);
                break;
            default: // User is trying to take an invalid action, like "kdlfsaghiufdshgi"
        }
    }

    // This will send a message to any player who is subscribed to the websocket
    // /player/<player-id>
    // player-id is generated when a player is created (i.e. when they connect to a
    // game) and passed to the frontend.
    public static void sendGameState(BaseClientGameState gameState, String playerId) {
        simpMessagingTemplate.convertAndSendToUser(playerId, "/game", gameState);
    }

    public static void sendQueueState(String playerId, int positionInQueue, int numWaitingPlayers) {
        simpMessagingTemplate.convertAndSendToUser(
                playerId,
                "/queue",
                new QueueState(positionInQueue, numWaitingPlayers));
    }
}

package com.revature.project2backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.revature.GameLogic.AllGames.BaseGame;
import com.revature.GameLogic.AllGames.GameRegistry;
import com.revature.GameLogic.AllGames.BaseGame.GameRepresentation;
import com.revature.GameLogic.Blackjack.BlackjackClientGameState;
import com.revature.GameLogic.Blackjack.BlackjackGame;
import com.revature.GameLogic.Blackjack.BlackjackPlayer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Controller
public class GameController {
    @Autowired
    private SimpMessagingTemplate simpMessageingTemplate;

    @GetMapping("allGames")
    public List<GameRepresentation> getAllGames() {
        return GameRegistry.getGameRegistry().getPublicGames();
    }

    @PostMapping("createblackjackGame")
    public String createBlackjackGame(@Header String lobbyName, @Header boolean lobbyIsPrivate) {
        BaseGame<BlackjackPlayer> newGame = new BlackjackGame(lobbyName, lobbyIsPrivate);
        GameRegistry.getGameRegistry().addNewGame(newGame);
        return newGame.getUrlSuffix();
    }

    @PutMapping("joinGame")
    public void joinBlackjackGame(@Payload String gameUrl, @Header("simpSessionId") String sessionId) {
        BaseGame game = GameRegistry.getGameRegistry().getGameByUrlSuffix(gameUrl);
        BlackjackGame blackjackGame;
        if(game instanceof BlackjackGame){
            blackjackGame = (BlackjackGame)game;
        } else {
            //Error condition - the user is using joinBlackjackGame to attempt to join a non-blackjack game
            return;
        }
        blackjackGame.addPlayer(new BlackjackPlayer(sessionId));
    }

    @MessageMapping("/app/gamestate")
    @SendTo("/blackjack/{tableID}")
    public BlackjackClientGameState updateGameStateForAllPlayer(@Payload String FIXME){
        return null;
    }

    //FIXME: Change these specific points to things that make more sense.
    @MessageMapping("/app/player")
    @SendToUser("/player/{sessionId}")
    public void sendPlayerBlackjackGameState(BlackjackClientGameState state, @Header("simpSessionId") String sessionId){
        simpMessageingTemplate.convertAndSendToUser(sessionId, "", state);
    }
}

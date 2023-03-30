package com.revature.project2backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.revature.game_logic.common.BaseGame;
import com.revature.game_logic.common.GameRegistry;
import com.revature.game_logic.common.BaseGame.GameRepresentation;

import java.util.List;

import org.springframework.http.ResponseEntity;

@CrossOrigin(originPatterns = "*")
@RestController
public class GameController {
    
    @GetMapping("allGames")
    public List<GameRepresentation> getAllGames() {
        return GameRegistry.getPublicGames();
    }

    @GetMapping("amIHost")
    public ResponseEntity<Boolean> getPlayerIsHost(@RequestHeader String gameId, @RequestHeader String playerId){
        BaseGame<?> game = GameRegistry.getGameByUrlSuffix(gameId);
        if(game != null){
            return ResponseEntity.status(200).body(game.isPlayerHost(playerId));
        }
        return ResponseEntity.status(404).body(false);
    }
}

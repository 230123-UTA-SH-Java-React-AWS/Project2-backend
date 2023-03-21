package com.revature.GameLogic.AllGames;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.revature.game_logic.common.BaseGame;

public class BaseGameTest {
    BaseGame baseGame;

    @BeforeAll
    public void setUp() {
        baseGame = Mockito.mock(BaseGame.class, Mockito.CALLS_REAL_METHODS);
    }
    
    @Test
    void testUpdatePlayers() {

    }
}

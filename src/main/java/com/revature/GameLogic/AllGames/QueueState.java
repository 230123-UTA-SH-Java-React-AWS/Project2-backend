package com.revature.GameLogic.AllGames;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor class QueueState {
    private int positionInQueue;
    private int numWaitingPlayers;
}

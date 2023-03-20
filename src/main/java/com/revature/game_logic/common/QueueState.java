package com.revature.game_logic.common;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor class QueueState {
    private int positionInQueue;
    private int numWaitingPlayers;
}

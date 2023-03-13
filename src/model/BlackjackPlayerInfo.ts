import { Card52 } from "./Card52"

export interface BlackjackPlayerInfo {
    endGameState: string,
    playerName: string,
    hasTakenTurn: boolean,
    cards: Card52[]
};
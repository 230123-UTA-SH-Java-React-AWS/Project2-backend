import { Card52 } from "./Card52"
import { BlackjackPlayerInfo } from "./BlackjackPlayerInfo"

export interface BlackjackClientGameState {
    dealersCards: Card52[],
    players: BlackjackPlayerInfo[]
};
import { Card52 } from "./Card52"

export class BlackjackPlayerInfo {
    endGameState: string;
    playerName: string;
    hasTakenTurn: boolean;
    cards: Card52[];
    isHost: boolean;

    constructor(endGameState: string, playerName: string, hasTakenTurn: boolean, cards: Card52[], isHost: boolean) {
        this.endGameState = endGameState;
        this.playerName = playerName;
        this.hasTakenTurn = hasTakenTurn;
        this.cards = cards;
        this.isHost = isHost;
    }
};
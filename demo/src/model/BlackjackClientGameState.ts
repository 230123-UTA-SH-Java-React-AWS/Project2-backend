import { BlackjackPlayerInfo } from "./BlackJackPlayerInfo";
import { Card52 } from "./Card52";

export class BlackjackClientGameState {
    dealersCards: Card52[];
    players: BlackjackPlayerInfo[];
    
    constructor(dealersCards: Card52[], players: BlackjackPlayerInfo[]) {
        this.dealersCards = dealersCards;
        this.players = players;
        
    }
}
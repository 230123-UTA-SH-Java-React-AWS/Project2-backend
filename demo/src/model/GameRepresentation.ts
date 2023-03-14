export class GameRepresentation {
    gameType: string;
    urlSuffix: string;
    gameName: string;
    numActivePlayers: number;
    numMaxPlayers: number;
    numWaitingPlayers: number

    constructor(
        gameType: string,
        urlSuffix: string,
        gameName: string,
        numActivePlayers: number,
        numMaxPlayers: number,
        numWaitingPlayers: number) {
        this.gameType = gameType;
        this.urlSuffix = urlSuffix;
        this.gameName = gameName;
        this.numActivePlayers = numActivePlayers;
        this.numMaxPlayers = numMaxPlayers;
        this.numWaitingPlayers = numWaitingPlayers;
    }
}
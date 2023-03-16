import axios, { AxiosRequestConfig } from "axios";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { GameRepresentation } from "../model/GameRepresentation";
import { BASE_URL, GAME_PORT } from "../static/defaults";

function GameRoom() {
    const [games, setGames] = useState<GameRepresentation[] | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `${BASE_URL}:${GAME_PORT}`,
            headers: {
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/allGame';

        axios.get(PATH, requestConfig)
        .then( (res) => setGames(res.data.json))
        .catch( (err) => console.log(err));

    }, []);

    const navigateToGame = (gameType:string, gameId:string) => {
        navigate('/' + gameType.toLowerCase() + '/' + gameId);
    }

    return (
        <>
            <ul>
                {games?.map((game) => <li key={game.gameId} onClick={() => navigateToGame(game.gameType, game.gameId)}>{game.gameName}: {game.numActivePlayers}/{game.numMaxPlayers}</li>)}
            </ul>
            <Link to='/newGame'>Create A Game</Link>
        </>
    );
}

export default GameRoom;
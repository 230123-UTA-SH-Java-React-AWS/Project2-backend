import axios, { AxiosRequestConfig } from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { BASE_URL, GAME_PORT } from "../static/defaults";

function NewGame() {
    const [gameType, setGameType] = useState<string>("");
    const [gameName, setGameName] = useState<string>("");
    const [isPrivate, setIsPrivate] = useState<boolean>(false);
    const navigate = useNavigate();

    const handleGameType = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setGameType(e.target.value);
    }

    const handleGameName = (e: React.ChangeEvent<HTMLInputElement>) => {
        setGameName(e.target.value);
    }

    const handleNewGame = (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        
        const requestConfig: AxiosRequestConfig = {
            baseURL: `${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameName': gameName,
                'lobbyIsPrivate': isPrivate,
                'Content-Type': 'application/json'
            }
        }

        const PATH = `/join${gameType}Game`;

        axios.post(PATH, {}, requestConfig)
        .then( (res) => navigate('/'))
        .catch( (err) => console.log(err));
    }

    return (
        <>
            <form>
                <input type='text' onChange={(e) => handleGameName(e)}/>
                <select onChange={(e) => handleGameType(e)}>
                    <option value="">New Game Options</option>
                    <option value="Blackjack">Blackjack</option>
                </select>
                <button type="submit" onClick={(e) => {
                    setIsPrivate(false);
                    handleNewGame(e)}
                    }>Host a new public game</button>
                <button type="submit" onClick={(e) => {
                    setIsPrivate(true);
                    handleNewGame(e)}
                    }>Host a new private game</button>
            </form>
        </>);

}

export default NewGame;
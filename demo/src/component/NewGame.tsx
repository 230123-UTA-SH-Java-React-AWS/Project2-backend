import { useState } from "react";
import { useNavigate } from "react-router-dom";

function NewGame() {
    const [gameType, setGameType] = useState<string>("");
    const [gameName, setGameName] = useState<string>("");
    const [isPrivate, setIsPrivate] = useState<boolean>(false);
    const navigate = useNavigate();

    const joinAndRedirect = (url:string) => {
        fetch(`http://localhost:8080/joinGame`, {
            method: "PUT",
            body: url
        })
        .then( () => navigate('/' + gameType + '/' + url))
        .catch( (err) => console.log(err));
    } 

    const handleNewGame = (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();

        fetch(`http://localhost:8080/create${gameType}Game`, {
            method: "POST",
            headers: {
                "gameName": `${gameName}`,
                "lobbyIsPrivate": `${isPrivate}`
            },

        })
        .then( (res) => (joinAndRedirect(res.toString())))
        .catch( (err) => console.log(err));
    }

    return (
        <>
            <form>
                <input type='text' />
                <select>
                    <option value="">New Game Options</option>
                    <option value="blackjack">Blackjack</option>
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
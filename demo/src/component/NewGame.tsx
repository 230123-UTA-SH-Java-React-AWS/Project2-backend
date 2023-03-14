import { useState } from "react";
import { useNavigate } from "react-router-dom";

function NewGame() {
    const [gameType, setGameType] = useState<string>("");
    const navigate = useNavigate();
    const handleNewGame = (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        fetch(`http://localhost:8080/create${gameType}Game`)
        .then( (res) => (navigate('/' + gameType + '/' + res.toString())))
        .catch( (err) => console.log(err));
    }

    return (
        <>
            <form>
                <select>
                    <option value="">New Game Options</option>
                    <option value="blackjack">Blackjack</option>
                </select>
                <button type="submit" onClick={(e) => handleNewGame(e)}>Create New Game</button>
            </form>
        </>);

}

export default NewGame;
import { useState } from 'react';
import { over } from 'stompjs';
import { BlackjackClientGameState } from "./model/BlackjackClientGameState"
import SockJS from 'sockjs-client';
let stompClient = null;
function BlackJackTable() {
    const [gameUrl, setGameUrl] = useState("");
    const [isActive, setIsActive] = useSate(false);
    const [gameState, setGameState] = useState({
        dealersCards:[],
        players:[]
    });

    useEffect(() => {
        fetch(`http://localhost:8080/createGame`)
        .then( (res) => setGameUrl(res))
        .catch( (err) => console.log(err));
    }, []);

    const connect = () => {
        let socket = new SockJS(`http://localhost:8080/ws`);
        stompClient = over(socket);
        //TODO: remove console.log below
        stompClient.connect({}, onConnect, (e) => { console.log(e) });
    };

    //Axios request
    // ask the backend to create a game
    // once the backend creates a game it will pass the URL back to here, and then we connect to it with the new URL provided
    //  connecting to the new game endpoint will give us a new player endpoint?

    const onConnect = () => {
        stompClient.subscribe('/blackjack/' + gameUrl, (payload) => { console.log(payload) });
        //Player subscription should be controlled by their session ID
        stompClient.subscribe('/player/' + playerData.username, (payload) => { 
            if((typeof payload) == "BlackjackClientGameState") {
                setGameState(payload);
            }
            console.log(payload) 
        });
    };

    const onHitAction = () => {
        stompClient.send('/app/gamestate', {}, JSON.stringify({cards: [{suit:CLUB, rank:TWO}, {suit:CLUB, rank:THREE}]}));
    }
    
    const playerList = gameState.players.map(player => 
        <li>
            {player.playerName} has {player.cards} and has{player.hasTakenTurn? "" : " not"} finished drawing cards.
        </li>);
    return (
        <div>
            <ul>
                <li>
                    Dealer has {playerData.dealersCards}
                </li>
                {playerList}
            </ul>
            {isActive ? (<button type='button' onClick={onHitAction}>HIT</button>) : (<button type='button' onClick={connect}>Connect</button>)}
        </div>
    );
}

export default BlackJackTable;
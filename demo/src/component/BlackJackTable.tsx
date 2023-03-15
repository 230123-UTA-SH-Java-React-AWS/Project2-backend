import { useState, useEffect } from 'react';
import { Client, over } from 'stompjs';
import SockJS from 'sockjs-client';
import { BlackjackClientGameState } from '../model/BlackjackClientGameState';
import { BlackjackPlayerInfo } from '../model/BlackjackPlayerInfo';
import { useParams } from 'react-router-dom';

let stompClient: Client;

function BlackJackTable() {
    const [playerId, setPlayerId] = useState<string>("");
    const [gameState, setGameState] = useState<BlackjackClientGameState>();

    let { tableId } = useParams();

    const connect = () => {
        fetch(`http://localhost:8080/joinGame`, {
            method: "PUT",
            body: tableId
        })
        .then( (res) => setPlayerId(res.toString()))
        .catch( (err) => console.log(err));

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
        stompClient.subscribe('/blackjack/' + tableId, (payload) => { console.log(payload) });
        //Player subscription should be controlled by their session ID
        stompClient.subscribe('/player/' + playerId, (payload) => {
            if(payload instanceof BlackjackClientGameState) {
                setGameState(new BlackjackClientGameState(payload.dealersCards, payload.players));
            }
            
            console.log(payload);
        });
    };

    const onHitAction = () => {
        stompClient.send('/app/gamestate', {}, JSON.stringify({"cards": [{"suit":"CLUB", "rank":"TWO"}, {"suit":"CLUB", "rank":"THREE"}]}));
    }

    const onStandAction = () => {}
    
    let playerList: any;
    if (typeof gameState != 'undefined') {
        playerList = gameState.players.map(player => 
            <li>
                {player.playerName} has {"" + player.cards} and has{player.hasTakenTurn? "" : " not"} finished drawing cards.
            </li>);
    }
    
    return (
        <div>
            <button type='button' onClick={onHitAction}>HIT</button>
            <button type='button' onClick={onStandAction}>STAND</button>
        </div>
    );
}

export default BlackJackTable;
import { useState, useEffect } from 'react';
import { Client, over } from 'stompjs';
import SockJS from 'sockjs-client';
import { BlackjackClientGameState } from '../model/BlackjackClientGameState';
import { BlackjackPlayerInfo } from '../model/BlackjackPlayerInfo';
import { useParams } from 'react-router-dom';

let stompClient: Client;

function BlackJackTable() {
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [playerId, setPlayerId] = useState<string>("");
    const [gameState, setGameState] = useState<BlackjackClientGameState>();

    let { tableId } = useParams();

    const connect = () => {
        let socket = new SockJS(`http://localhost:8080/ws`);
        stompClient = over(socket);
        //TODO: remove console.log below
        stompClient.connect({}, onConnected, (e) => { console.log(e) });
    };

    const onConnected = () => {
        setIsConnected(true);
        // stompClient.subscribe('/blackjack/' + tableId, (payload) => { console.log(payload) });
        //Player subscription should be controlled by their session ID
        stompClient.subscribe('/player/' + playerId + '/', (payload) => {
            // if(payload instanceof BlackjackClientGameState) {
            //     setGameState(new BlackjackClientGameState(payload.dealersCards, payload.players));
            // }
            
            console.log(payload);
        });
    };

    const onHitAction = () => {
       fetch(`http://localhost:8080/blackjackAction`, {
            method: "PUT",
            headers: {
                "sessionId": playerId,
                "actionVerb":"HIT"
            },
            body:tableId
        })
        .catch( (err) => console.log(err)); 
    }

    const onStandAction = () => {
        fetch(`http://localhost:8080/blackjackAction`, {
            method: "PUT",
            headers: {
                "sessionId": playerId,
                "actionVerb":"STAND"
            },
            body:tableId
        })
        .catch( (err) => console.log(err));
    }
    
    let playerList: any;
    if (typeof gameState != 'undefined') {
        playerList = gameState.players.map(player => 
            <li>
                {player.playerName} has {"" + player.cards} and has{player.hasTakenTurn? "" : " not"} finished drawing cards.
            </li>);
    }

    const handlePlayerId = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPlayerId(e.target.value);
    }
    
    return (
        <div>
            <button type='button' onClick={connect} disabled={isConnected}>Connect</button>
            <input type='text' onChange={(e) => handlePlayerId(e)} />
            <button type='button' onClick={onHitAction} disabled={!isConnected} >HIT</button>
            <button type='button' onClick={onStandAction} disabled={!isConnected} >STAND</button>
        </div>
    );
}

export default BlackJackTable;
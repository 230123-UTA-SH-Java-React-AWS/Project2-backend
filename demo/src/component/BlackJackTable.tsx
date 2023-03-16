import { useState, useEffect } from 'react';
import { Client, over } from 'stompjs';
import SockJS from 'sockjs-client';
import { BlackjackClientGameState } from '../model/BlackjackClientGameState';
import { BlackjackPlayerInfo } from '../model/BlackjackPlayerInfo';
import { useParams } from 'react-router-dom';
import { BASE_URL, GAME_PORT } from '../static/defaults';
import axios, { AxiosRequestConfig } from 'axios';

let stompClient: Client;

function BlackJackTable() {
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [playerId, setPlayerId] = useState<string>("");
    const [gameState, setGameState] = useState<BlackjackClientGameState>();

    let { tableId } = useParams();

    const connect = () => {
        let socket = new SockJS(`http://${BASE_URL}:${GAME_PORT}/ws`);
        stompClient = over(socket);
        //TODO: remove console.log below
        stompClient.connect({}, onConnected, (e) => { console.log(e) });
    };

    const disconnect = () => {
        setIsConnected(false);
        if (stompClient != null) {
            stompClient.disconnect(() => console.log("Disconnected"));
        }
    }

    const onConnected = () => {
        setIsConnected(true);
        stompClient.subscribe('/blackjack/' + tableId + '/', (payload) => { console.log(payload) });
        //Player subscription should be controlled by their session ID
        stompClient.subscribe('/player/' + playerId + '/blackjack', (payload) => {
            // if(payload instanceof BlackjackClientGameState) {
            //     setGameState(new BlackjackClientGameState(payload.dealersCards, payload.players));
            // }
            
            console.log(payload);
        });
    };

    const onHitAction = () => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `${BASE_URL}:${GAME_PORT}`,
            headers: {
                'sessionId': playerId,
                'actionVerb':"HIT",
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/blackjackAction';

        axios.put(PATH, {
        tableId
        }, requestConfig)
        .catch( (err) => console.log(err));
    }

    const onStandAction = () => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `${BASE_URL}:${GAME_PORT}`,
            headers: {
                'sessionId': playerId,
                'actionVerb':"STAND",
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/blackjackAction';

        axios.put(PATH, {
        tableId
        }, requestConfig)
        .catch( (err) => console.log(err));
    }
    
    let playerList: any;
    if (typeof gameState != 'undefined') {
        playerList = gameState.players.map(player => 
            <li>
                {player.playerName} has {"" + player.cards} and has{player.hasTakenTurn? "" : " not"} finished drawing cards.
            </li>);
    }

    const handleJoinGame = () => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameId': tableId,
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/joinblackjackGame';

        axios.put(PATH, {
        tableId
        }, requestConfig)
        .then( (res) => setPlayerId(res.data))
        .catch( (err) => console.log(err));
    }

    const handleStartGame = () => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameId': tableId,
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/startblackjackGame';

        axios.put(PATH, {
        tableId
        }, requestConfig)
        .then( (res) => console.log(res.data))
        .catch( (err) => console.log(err));
    }
    
    return (
        <div>
            <button type='button' onClick={connect} disabled={isConnected}>Connect</button>
            <button type='button' onClick={disconnect} disabled={!isConnected}>Disconnect</button>
            <button type='button' onClick={handleJoinGame}>Join Game</button>
            <button type='button' onClick={handleStartGame}>Start Game</button>
            {/* <button type='button' onClick={onHitAction} disabled={!isConnected} >HIT</button>
            <button type='button' onClick={onStandAction} disabled={!isConnected} >STAND</button> */}
        </div>
    );
}

export default BlackJackTable;
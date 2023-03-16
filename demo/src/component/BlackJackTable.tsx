import { useState, useEffect } from 'react';
import { Client, over } from 'stompjs';
import SockJS from 'sockjs-client';
import { BlackjackClientGameState } from '../model/BlackjackClientGameState';
import { BlackjackPlayerInfo } from '../model/BlackjackPlayerInfo';
import { useParams } from 'react-router-dom';
import { BASE_URL, GAME_PORT } from '../static/defaults';
import axios, { AxiosRequestConfig } from 'axios';
import { QueueState } from '../model/QueueState';

let stompClient: Client;

function BlackJackTable() {
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [playerId, setPlayerId] = useState<string>("");
    const [gameState, setGameState] = useState<BlackjackClientGameState>();
    const [queueState, setQueueState] = useState<QueueState>();

    let { tableId } = useParams();

    useEffect(() => {
        //First, we join the game. This gives us a player token.
        joinGame();
        //Next, we subscribe to the two endpoints, to get game state and queue position updates.
        //We do these at the same time because a player may automatically be moved from the queue to the game
        //connect() is called from inside of joinGame because it must be done asynchronously.
    }, []);
    

    const connect = () => {
        let socket = new SockJS(`http://${BASE_URL}:${GAME_PORT}/ws`);
        stompClient = over(socket);
        // TODO: remove console.log below
        setTimeout(() => {
            stompClient.connect({}, () => {
                console.log("We're connected!");
                onConnected();
            }, (e) => { console.log("Error: " + e) });
        }, 100);
    };

    const disconnect = () => {
        setIsConnected(false);
        if (stompClient != null) {
            stompClient.disconnect(() => console.log("Disconnected"));
        }
    }

    const onConnected = () => {
        setIsConnected(true);
        stompClient.subscribe('/player/' + playerId + '/queue', (payload) => { 
            console.log(payload) 
        });
        //Player subscription should be controlled by their session ID
        stompClient.subscribe('/player/' + playerId + '/game', (payload) => {
            // if(payload instanceof BlackjackClientGameState) {
            //     setGameState(new BlackjackClientGameState(payload.dealersCards, payload.players));
            // }
            
            console.log(payload);
        });
        console.log("We are now subscribed??");
        
    };

    const onHitAction = () => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `http://${BASE_URL}:${GAME_PORT}`,
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
            baseURL: `http://${BASE_URL}:${GAME_PORT}`,
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
    
    // let playerList: any;
    // if (typeof gameState != 'undefined') {
    //     playerList = gameState.players.map(player => 
    //         <li>
    //             {player.playerName} has {"" + player.cards} and has{player.hasTakenTurn? "" : " not"} finished drawing cards.
    //         </li>);
    // }

    function joinGame() {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `http://${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameId': tableId,
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/joinBlackjackGame';

        axios.put<string>(PATH, {
        tableId
        }, requestConfig)
        .then( (res) => {
            setPlayerId(res.data);
            connect();
        })
        //.then( () => {connect()})
        .catch( (err) => console.log(err));
    }

    const handleStartGame = () => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `http://${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameId': tableId,
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/startBlackjackGame';

        axios.put(PATH, {
        tableId
        }, requestConfig)
        .then( (res) => console.log(res.status))
        .catch( (err) => console.log(err));
    }
    
    return (
        <div>
            <button type='button' onClick={handleStartGame}>Start Game</button>
            {/* <button type='button' onClick={onHitAction} disabled={!isConnected} >HIT</button>
            <button type='button' onClick={onStandAction} disabled={!isConnected} >STAND</button> */}
        </div>
    );
}

export default BlackJackTable;
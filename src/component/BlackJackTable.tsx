import { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import { BlackjackClientGameState } from '../model/BlackjackClientGameState';
import { useParams } from 'react-router-dom';
import { BASE_URL, GAME_PORT } from '../static/defaults';
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import { QueueState } from '../model/QueueState';

let stompClient: Client;

function BlackJackTable() {
    const [isJoined, setIsJoined] = useState<boolean>(false);
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [playerId, setPlayerId] = useState<string>("");
    const [gameState, setGameState] = useState<BlackjackClientGameState>();
    const [queueState, setQueueState] = useState<QueueState>();

    let { tableId } = useParams();

    useEffect(() => {(async () => {
        let response = await handleJoinGame();
        setPlayerId(response.data);
        setIsJoined(true);
        stompClient = new Client({
            brokerURL: `ws://${BASE_URL}:${GAME_PORT}/ws`,
            onConnect: (frame) => {
                if (stompClient.connected) {
                    console.log("Connecting websocket: " + frame);
                    let destination = '/user/' + response.data + '/queue';
                    stompClient.subscribe(destination, (payload) => { 
                        console.log(payload);
                    });
                    console.log(destination);
                    
                    //Player subscription should be controlled by their session ID
                    destination = '/user/' + response.data + '/game';
                    stompClient.subscribe(destination, (payload) => {
                        // if(payload instanceof BlackjackClientGameState) {
                        //     setGameState(new BlackjackClientGameState(payload.dealersCards, payload.players));
                        // }
                        
                        console.log(payload);
                    });
                    console.log(destination);
                } else {
                    //TODO handle connection failure
                }
            },
            onDisconnect: (frame) => {
                console.log("Disconnecting websocket: " + frame);
            },
            onStompError: (frame) => {
                console.log('Broker reported error: ' + frame.headers['message']);
                console.log('Additional details: ' + frame.body);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000
        });
        
        stompClient.activate();
        console.log("Connected");
        // let socket = new SockJS(`http://${BASE_URL}:${GAME_PORT}/ws`);
        // stompClient = over(socket);
        // TODO: remove console.log below
        setIsConnected(true);
    })();
        
        return () => {
            setIsConnected(false);
            if(stompClient != null) {
                stompClient.deactivate();
            }
        };
    }, []);

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

    const handleJoinGame = async ():Promise<AxiosResponse<string,any>> => {
        const requestConfig: AxiosRequestConfig = {
            baseURL: `http://${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameId': tableId,
                'Content-Type': 'application/json'
            }
        }

        const PATH = '/joinBlackjackGame';

        const response = await axios.put<string>(PATH, {
        tableId
        }, requestConfig);
        return response;
        
    }

    const handleStartGame = () => {
        console.log(stompClient);
        console.log(playerId);
        const requestConfig: AxiosRequestConfig = {
            baseURL: `http://${BASE_URL}:${GAME_PORT}`,
            headers: {
                'gameId': tableId,
                'playerId': playerId,
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
            <button type='button' onClick={handleJoinGame} disabled={isJoined}>Join Game</button>
            <button type='button' disabled={isConnected || !isJoined}>Connect</button>
            <button type='button' disabled={!isConnected}>Disconnect</button>
            <button type='button' onClick={handleStartGame} disabled={!isConnected}>Start Game</button>
            {/* <button type='button' onClick={onHitAction} disabled={!isConnected} >HIT</button>
            <button type='button' onClick={onStandAction} disabled={!isConnected} >STAND</button> */}
        </div>
    );
}

export default BlackJackTable;
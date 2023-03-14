import { useState, useEffect } from 'react';
import { Client, over } from 'stompjs';
import SockJS from 'sockjs-client';
import { BlackjackClientGameState } from '../model/BlackJackClientGameState';
import { BlackjackPlayerInfo } from '../model/BlackJackPlayerInfo';
import { useParams } from 'react-router-dom';

let stompClient: Client;

function BlackJackTable() {
    const [isActive, setIsActive] = useState<boolean>(false);
    const [gameState, setGameState] = useState<BlackjackClientGameState>();

    let { tableId } = useParams();

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
        stompClient.subscribe('/blackjack/' + tableId, (payload) => { console.log(payload) });
        //Player subscription should be controlled by their session ID
        stompClient.subscribe('/player/', (payload) => {
            if(payload instanceof BlackjackClientGameState) {
                setGameState(new BlackjackClientGameState(payload.dealersCards, payload.players));
            }
            
            console.log(payload);
        });
    };

    const onHitAction = () => {
        stompClient.send('/app/gamestate', {}, JSON.stringify({"cards": [{"suit":"CLUB", "rank":"TWO"}, {"suit":"CLUB", "rank":"THREE"}]}));
    }
    let playerList: any;
    if (typeof gameState != 'undefined') {
        playerList = gameState.players.map(player => 
            <li>
                {player.playerName} has {"" + player.cards} and has{player.hasTakenTurn? "" : " not"} finished drawing cards.
            </li>);
    }
    
    return (
        <div>
            <ul>
                {typeof gameState != 'undefined' &&
                <li>
                    Dealer has {"" + gameState.dealersCards}
                </li>}
                
                {playerList}
            </ul>
            {isActive ? (<button type='button' onClick={onHitAction}>HIT</button>) : (<button type='button' onClick={connect}>Connect</button>)}
        </div>
    );
}

export default BlackJackTable;
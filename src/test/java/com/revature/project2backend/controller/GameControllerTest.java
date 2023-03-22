package com.revature.project2backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.GameLogic.Blackjack.BlackjackClientGameState;
import com.revature.GameLogic.Blackjack.BlackjackGame;
import com.revature.GameLogic.Blackjack.BlackjackPlayer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {
    private WebSocketStompClient webSocketStompClient;

    @LocalServerPort
    private Integer port;

    @Autowired
    private GameController gameController;

    private JacksonTester<BlackjackClientGameState> jsonGameState;

    BlackjackGame newGame;
    BlackjackPlayer player;

    @BeforeEach
    void setup() {
        this.webSocketStompClient = new WebSocketStompClient(
                new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        JacksonTester.initFields(this, new ObjectMapper());

        String gameName = "test";
        boolean lobbyIsPrivate = false;

        newGame = new BlackjackGame(gameName, lobbyIsPrivate);
        player = new BlackjackPlayer(gameController.getSimpMessagingTemplate());
    }

    @Test
    void contextsLoad() {
        Assertions.assertNotNull(gameController.getSimpMessagingTemplate());
    }

    @Test
    void verifyGameStateIsReceivedOnStartBlackjackGame()
            throws InterruptedException, ExecutionException, TimeoutException, IOException {

        BlockingQueue<BlackjackClientGameState> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("http://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe("/player/" + player.getPlayerId() + "/game", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BlackjackClientGameState.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((BlackjackClientGameState) payload);
            }
        });

        newGame.addPlayer(player);
        newGame.dealHands();

        Assertions.assertNotNull(player.getClientGameState());
        Assertions.assertEquals(jsonGameState.write(player.getClientGameState()).getJson(),
                jsonGameState.write(blockingQueue.poll(1, TimeUnit.SECONDS)).getJson());
    }

    @Test
    void verifyGameStateIsReceivedOnBlackjackGameHitAction()
            throws InterruptedException, ExecutionException, TimeoutException, IOException {

        BlockingQueue<BlackjackClientGameState> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("http://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe("/player/" + player.getPlayerId() + "/game", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BlackjackClientGameState.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((BlackjackClientGameState) payload);
            }
        });

        newGame.addPlayer(player);
        newGame.dealHands();
        newGame.onPlayerHit(player.getPlayerId());

        Assertions.assertEquals(jsonGameState.write(player.getClientGameState()).getJson(),
                jsonGameState.write(blockingQueue.poll()).getJson());
    }

    @Test
    void verifyGameStateIsReceivedOnBlackjackGameStandAction()
            throws InterruptedException, ExecutionException, TimeoutException, IOException {

        BlockingQueue<BlackjackClientGameState> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("http://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe("/player/" + player.getPlayerId() + "/game", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BlackjackClientGameState.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((BlackjackClientGameState) payload);
            }
        });

        newGame.addPlayer(player);
        newGame.dealHands();
        newGame.onPlayerStand(player.getPlayerId());

        Assertions.assertEquals(jsonGameState.write(player.getClientGameState()).getJson(),
                jsonGameState.write(blockingQueue.poll()).getJson());
    }
}

package com.revature.project2backend.controller;

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
import com.revature.GameLogic.AllGames.BaseClientGameState;
import com.revature.GameLogic.Blackjack.BlackjackClientGameState;
import com.revature.GameLogic.Blackjack.BlackjackGame;
import com.revature.GameLogic.Blackjack.BlackjackPlayer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.awaitility.Awaitility;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {
    private WebSocketStompClient webSocketStompClient;

    @LocalServerPort
    private Integer port;

    GameController gameController = Mockito.mock(GameController.class);

    private JacksonTester<BaseClientGameState> jsonGameState;

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
        player = new BlackjackPlayer(gameController);
    }

    @Test
    void verifyInitailGameStateIsSent() throws InterruptedException, ExecutionException, TimeoutException {
        CountDownLatch latch = new CountDownLatch(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe("/player/" + player.getPlayerId() + "/game", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return BlackjackClientGameState.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                latch.countDown();
            }
        });

        Awaitility.await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(0, latch.getCount()));
    }

    @Test
    void verifyInitailGameStateIsReceived() throws InterruptedException, ExecutionException, TimeoutException {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe("/player/" + player.getPlayerId() + "/game", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((String) payload);
            }
        });

        newGame.dealHands();

        Awaitility.await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> Assertions
                .assertEquals(jsonGameState.write(player.getClientGameState()).getJson(), blockingQueue.poll()));
    }
}

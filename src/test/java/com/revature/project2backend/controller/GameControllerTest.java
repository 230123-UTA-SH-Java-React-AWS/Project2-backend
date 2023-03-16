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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.GameLogic.AllGames.BaseClientGameState;
import com.revature.GameLogic.AllGames.BaseGame;
import com.revature.GameLogic.AllGames.GameRegistry;
import com.revature.GameLogic.AllGames.IdGenerator;
import com.revature.GameLogic.Blackjack.BlackjackGame;
import com.revature.GameLogic.Blackjack.BlackjackPlayer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.http.parser.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {
    private WebSocketStompClient webSocketStompClient;

    @LocalServerPort
    private Integer port;

    GameController gameController = Mockito.mock(GameController.class);

    private JacksonTester<BaseClientGameState> jsonGameState;

    @BeforeEach
    void setup() {
        this.webSocketStompClient = new WebSocketStompClient(
                new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        JacksonTester.initFields(this, new ObjectMapper());
    }

    // WIP
    @Test
    void verifyInitailGameStateIsReceived() throws InterruptedException, ExecutionException, TimeoutException {

        String gameName = "test";
        boolean lobbyIsPrivate = false;

        BaseGame<BlackjackPlayer> newGame = new BlackjackGame(gameName, lobbyIsPrivate);

        BlackjackPlayer player = new BlackjackPlayer(gameController);
        newGame.addPlayer(player);

        String playerId = IdGenerator.generate_id();

        BlockingQueue<BaseClientGameState> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {
                }).get(1, TimeUnit.SECONDS);

        session.subscribe("/player/" + playerId + "/game", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((BaseClientGameState) payload);
            }
        });

    }
}

package com.revature.project2backend.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.revature.project2backend.entity.Hand52;

@Component
public class SocketHandler extends TextWebSocketHandler {
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        for (WebSocketSession webSocketSession : sessions) {
            System.out.println(message.getPayload());
            Hand52 value = new Gson().fromJson(message.getPayload(), Hand52.class);

            webSocketSession.sendMessage(new TextMessage("Hello " + value.getCards().toString() + " !"));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

}

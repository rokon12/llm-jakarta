package ca.bazlur.workshop.jakarta.hello;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class ChatWebSocket {
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String botResponse = getBotResponse(message); // Replace with LLM integration

        try {
            session.getBasicRemote().sendText("Bot: " + botResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    private String getBotResponse(String userMessage) {
        try {
            Thread.sleep(2000); // Delay for 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "You said: " + userMessage + ". I'm a bot!";
    }
}

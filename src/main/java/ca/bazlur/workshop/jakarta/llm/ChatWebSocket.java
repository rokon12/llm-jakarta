package ca.bazlur.workshop.jakarta.llm;

import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@ServerEndpoint("/chat")
public class ChatWebSocket {
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    @Inject
    private LangChainService langChainService;

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        langChainService.sendMessage(message, (next) -> {
            try {
                session.getBasicRemote().sendText(next);
            } catch (IOException e) {
                log.error("Error occurred", e);
            }
        });
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
}

package com.khanhbq.socket;

import io.socket.engineio.server.EngineIoWebSocket;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SpringEngineIoWebSocketHandler extends EngineIoWebSocket {

    public static final String ATTRIBUTE_ENGINE_IO_BRIDGE = "engine.io.bridge";
    public static final String ATTRIBUTE_ENGINE_IO_QUERY = "engine.io.query";
    public static final String ATTRIBUTE_ENGINE_IO_HEADERS = "engine.io.headers";

    public static final String ATTRIBUTE_ENGINE_SUBJECT = "test.subject";

    private final WebSocketSession mSession;
    private final Map<String, String> mQuery;
    private final Map<String, List<String>> mHeaders;
    private final String subject;

    public SpringEngineIoWebSocketHandler(WebSocketSession session) {
        this.mSession = session;
        this.mQuery = (Map<String, String>) mSession.getAttributes().get(ATTRIBUTE_ENGINE_IO_QUERY);
        this.mHeaders = (Map<String, List<String>>) mSession.getAttributes().get(ATTRIBUTE_ENGINE_IO_HEADERS);
        this.subject = (String) mSession.getAttributes().get(ATTRIBUTE_ENGINE_SUBJECT);
    }

    @Override
    public Map<String, String> getQuery() {
        return mQuery;
    }

    @Override
    public Map<String, List<String>> getConnectionHeaders() {
        return mHeaders;
    }

    @Override
    public void write(String s) throws IOException {
        mSession.sendMessage(new TextMessage(s));
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        mSession.sendMessage(new BinaryMessage(bytes));
    }

    @Override
    public void close() {
        try {
            mSession.close();
        } catch (Exception ex) {
            // log
        }
    }

    public void handleMessage(WebSocketMessage<?> message) {
        if (message.getPayload() instanceof String || message.getPayload() instanceof byte[]) {
            emit("message", message.getPayload());
        } else {
            throw new RuntimeException(String.format(
                    "Invalid message type received: %s. Expected String or byte[].",
                    message.getPayload().getClass().getName()));
        }
    }

    public void handleTransportError(Throwable exception) {

    }

    public void afterConnectionClosed(CloseStatus closeStatus) {
        emit("close");
    }
}

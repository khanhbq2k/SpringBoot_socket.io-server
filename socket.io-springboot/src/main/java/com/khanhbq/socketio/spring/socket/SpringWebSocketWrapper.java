package com.khanhbq.socketio.spring.socket;

import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.utils.ParseQS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpringWebSocketWrapper implements HandshakeInterceptor, WebSocketHandler {

    private final EngineIoServer engineIoServer;

    @RequestMapping(
            value = "/chat/ws",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
            headers = {"Connection!=Upgrade", "Connection!=upgrade"})
    @CrossOrigin("*")
    public void httpHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> mQuery = Optional.ofNullable(request.getQueryString()).map(ParseQS::decode).orElse(Collections.emptyMap());
        // replace with your spring security authentication here
        String subject = mQuery.get("subject");
        request.setAttribute(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_SUBJECT, subject);
        engineIoServer.handleRequest(request, response);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        final SpringEngineIoWebSocketHandler webSocket = new SpringEngineIoWebSocketHandler(session);
        session.getAttributes().put(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_IO_BRIDGE, webSocket);
        engineIoServer.handleWebSocket(webSocket);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        ((SpringEngineIoWebSocketHandler) session.getAttributes().get(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_IO_BRIDGE))
                .handleMessage(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        ((SpringEngineIoWebSocketHandler) session.getAttributes().get(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_IO_BRIDGE))
                .handleTransportError(exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        ((SpringEngineIoWebSocketHandler) session.getAttributes().get(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_IO_BRIDGE))
                .afterConnectionClosed(closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        try {
            Map<String, String> mQuery = new HashMap<>();
            final String queryString = request.getURI().getQuery();
            if (queryString != null) {
                mQuery = ParseQS.decode(queryString);
            }
            String subject = mQuery.get("subject");
            attributes.put(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_SUBJECT, subject);
            attributes.put(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_IO_QUERY, mQuery);
            attributes.put(SpringEngineIoWebSocketHandler.ATTRIBUTE_ENGINE_IO_HEADERS, request.getHeaders());
            return true;
        } catch (Exception ex) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}

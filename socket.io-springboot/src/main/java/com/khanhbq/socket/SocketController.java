package com.khanhbq.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoSocket;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocketController implements CommandLineRunner {

    private final SocketIoNamespace mainNamespace;
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... xargs) {
        mainNamespace.on("connection", args -> {
            SocketIoSocket socket = (SocketIoSocket) args[0];
            String subject = socket.getInitialQuery().get("subject");
            sessionManager.addSession(socket.getId(), subject);
            socket.on("disconnect", arr -> {
                sessionManager.removeSession(socket.getId());
            });
            // The event and ChatMessage format correspond to {@link resources/test.html}
            socket.on("chat", arr -> {
                ChatMessage data;
                try {
                    data = objectMapper.readValue(arr[0].toString(), ChatMessage.class);
                    // broadcast all rooms
                    socket.broadcast(null, "chat", new JSONObject(objectMapper.writeValueAsString(data)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("data: " + data.getMsg());
            });
        });
    }
}

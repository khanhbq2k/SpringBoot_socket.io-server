package com.khanhbq.socket.config;

import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoServerOptions;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SocketIoEngineConfiguration {

    @Bean
    public EngineIoServer engineIoServer() {
        var opt = EngineIoServerOptions.newFromDefault();
        opt.setCorsHandlingDisabled(true);
        var eioServer = new EngineIoServer(opt);
        return eioServer;
    }

    @Bean
    public SocketIoServer socketIoServer(EngineIoServer eioServer) {
        var sioServer = new SocketIoServer(eioServer);
        return sioServer;
    }

    @Bean
    @Primary
    public SocketIoNamespace mainNamespace(SocketIoServer server) {
        var namespace = server.namespace("/");
        return namespace;
    }
}

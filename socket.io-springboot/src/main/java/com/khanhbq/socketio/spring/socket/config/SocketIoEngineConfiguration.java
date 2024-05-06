package com.khanhbq.socketio.spring.socket.config;

import com.khanhbq.socketio.spring.distribution.DistributedMachineIdGenerator;
import com.khanhbq.socketio.spring.socket.adapter.RedisAdapter;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoServerOptions;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoServer;
import io.socket.socketio.server.SocketIoServerOptions;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class SocketIoEngineConfiguration {

    @Bean
    public EngineIoServer engineIoServer() {
        EngineIoServerOptions opt = EngineIoServerOptions.newFromDefault();
        opt.setCorsHandlingDisabled(true);
        return new EngineIoServer(opt);
    }

    @Bean
    public SocketIoServer socketIoServer(EngineIoServer eioServer,
                                         RedissonClient redissonClient,
                                         DistributedMachineIdGenerator distributedMachineIdGenerator) {
        SocketIoServerOptions options = SocketIoServerOptions.newFromDefault()
                .setAdapterFactory(socketIoNamespace ->
                        new RedisAdapter(socketIoNamespace, redissonClient, distributedMachineIdGenerator)
                );
        SocketIoServer sioServer = new SocketIoServer(eioServer, options);
        log.info("Init Socket IO Server");
        return sioServer;
    }

    @Bean
    @Primary
    public SocketIoNamespace mainNamespace(SocketIoServer server) {
        return server.namespace("/");
    }
}

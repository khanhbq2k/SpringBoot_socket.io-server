package com.khanhbq.socketio.spring.distribution.config;

import com.khanhbq.socketio.spring.distribution.DistributedMachineIdGenerator;
import com.khanhbq.socketio.spring.distribution.impl.RedisDistributedMachineIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedIdGeneratorConfig {

    @Bean
    public DistributedMachineIdGenerator distributedMachineIdGenerator(RedissonClient redissonClient,
                                                                       @Value("${distributed.server.group}") String serverGroup,
                                                                       @Value("${distributed.server.maximumCapacity}") int maximumServerCapacity) {
        return new RedisDistributedMachineIdGenerator(redissonClient, serverGroup, maximumServerCapacity);
    }
}

package com.khanhbq.distribution.identity.config;

import com.khanhbq.distribution.identity.DistributedMachineIdGenerator;
import com.khanhbq.distribution.identity.impl.RedisDistributedMachineIdGenerator;
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

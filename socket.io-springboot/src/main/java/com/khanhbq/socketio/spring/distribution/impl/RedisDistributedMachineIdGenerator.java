package com.khanhbq.socketio.spring.distribution.impl;

import com.khanhbq.socketio.spring.distribution.DistributedMachineIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RedisDistributedMachineIdGenerator implements DistributedMachineIdGenerator {

    private static final String PREFIX_LOCK = "RedisMachineIdLock";
    private static final String CACHE_NAME = "RedisMachineIdCache";

    private static final int LOCK_ACQUIRE_TIMEOUT_SECONDS = 30;
    private static final int CACHE_ENTRY_EXPIRATION_SECONDS = 25;

    private final int maximumServerCapacity;
    private final String serverGroup;
    private final String uniqueMachineIdentifier;

    private final RMapCache<Long, String> machineIdCache;
    private final RedissonClient redissonClient;

    private final AtomicLong id = new AtomicLong(-1);

    public RedisDistributedMachineIdGenerator(RedissonClient redissonClient, String serverGroup, int maximumServerCapacity) {
        this.redissonClient = redissonClient;
        this.serverGroup = serverGroup;
        this.machineIdCache = redissonClient.getMapCache(CACHE_NAME + ":" + serverGroup);
        this.maximumServerCapacity = maximumServerCapacity;
        this.uniqueMachineIdentifier = generateMachineId();
        obtainId();
    }

    @Override
    public Long getCurrentMachineId() {
        long currentId = id.get();
        if (currentId == -1) {
            throw new IllegalStateException("Machine ID not initialized");
        }
        return currentId;
    }

    @Override
    public Set<Long> getAllMachineIds() {
        return new HashSet<>(machineIdCache.keySet());
    }

    private void obtainId() {
        log.info("Attempting to obtain server machine ID...");
        for (long i = 1; i <= maximumServerCapacity; i++) {
            log.info("Checking machine ID {}", i);
            RLock lock = redissonClient.getLock(PREFIX_LOCK + ":" + serverGroup + ":" + i);
            try {
                if (lock.tryLock(LOCK_ACQUIRE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    if (!machineIdCache.containsKey(i)) {
                        log.info("Assigning machine ID {}", i);
                        machineIdCache.fastPut(i, uniqueMachineIdentifier, CACHE_ENTRY_EXPIRATION_SECONDS, TimeUnit.SECONDS);
                        id.set(i);
                        return;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while trying to acquire lock", e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        throw new IllegalStateException("Failed to obtain machine ID");
    }

    private String generateMachineId() {
        return UUID.randomUUID().toString();
    }

    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        long currentId = id.get();
        if (currentId == -1) {
            return;
        }

        String insId = machineIdCache.get(currentId);
        if (!uniqueMachineIdentifier.equals(insId)) {
            log.error("Machine ID conflict detected. Machine ID: {}, ID: {}", uniqueMachineIdentifier, currentId);
            obtainId();
            return;
        }
        machineIdCache.fastPut(currentId, uniqueMachineIdentifier, CACHE_ENTRY_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    }
}

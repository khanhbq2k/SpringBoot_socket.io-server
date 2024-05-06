package com.khanhbq.socketio.spring.distribution;

import java.util.Set;

public interface DistributedMachineIdGenerator {

    Long getCurrentMachineId();

    Set<Long> getAllMachineIds();
}

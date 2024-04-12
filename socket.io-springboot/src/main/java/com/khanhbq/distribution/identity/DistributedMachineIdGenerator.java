package com.khanhbq.distribution.identity;

import java.util.Set;

public interface DistributedMachineIdGenerator {

    Long getCurrentMachineId();

    Set<Long> getAllMachineIds();
}

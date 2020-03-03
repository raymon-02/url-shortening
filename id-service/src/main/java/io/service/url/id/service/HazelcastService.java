package io.service.url.id.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import com.hazelcast.cp.CPSubsystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

@Service
public class HazelcastService {

    private static final String SERVER_IDS = "ids_in_use";
    private static final String LOCK = "ids_in_use";

    @Autowired
    @Qualifier("hzInstance")
    private HazelcastInstance hazelcastInstance;

    public Lock getLock() {
        CPSubsystem cpSubsystem = hazelcastInstance.getCPSubsystem();
        return cpSubsystem.getLock(LOCK);
    }

    public Set<String> getIdsInUse() {
        ISet<String> serverIds = hazelcastInstance.getSet(SERVER_IDS);
        return new HashSet<>(serverIds);
    }

    public void saveServerIdAsUse(String id) {
        ISet<String> serverIds = hazelcastInstance.getSet(SERVER_IDS);
        serverIds.add(id);
    }

    public void deleteServerId(String id) {
        ISet<String> serverIds = hazelcastInstance.getSet(SERVER_IDS);
        serverIds.remove(id);
    }
}

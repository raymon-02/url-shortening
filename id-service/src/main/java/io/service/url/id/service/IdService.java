package io.service.url.id.service;

import io.service.url.id.exception.NoAvailableServerIdException;
import io.service.url.id.model.ServerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class IdService {

    private static final String IDS = "ABCDEFGHIJKLMNOPQRTSUBWXYZ";
    private static final Lock lock = new ReentrantLock();

    @Autowired
    private HazelcastService hazelcastService;

    public ServerId getServerId() {
        log.info("Getting server id...");
        lock.lock();
        try {
            Set<String> idsInUse = hazelcastService.getIdsInUse();
            for (int i = 0; i < IDS.length(); i++) {
                String id = IDS.substring(i, i + 1);
                if (!idsInUse.contains(id)) {
                    ServerId result = new ServerId(id.toUpperCase(), id.toLowerCase());
                    hazelcastService.saveServerIdAsUse(id);
                    log.info("Server id: {}", result);
                    return result;
                }
            }
            log.error("No available server id");
            throw new NoAvailableServerIdException("No available server id");
        } finally {
            lock.unlock();
        }
    }

    public void deleteServerId(String id) {
        log.info("Deleting server id '{}' from use...", id);
        lock.lock();
        try {
            hazelcastService.deleteServerId(id);
            log.info("Server id '{}' deleted from use", id);
        } finally {
            lock.unlock();
        }
    }
}

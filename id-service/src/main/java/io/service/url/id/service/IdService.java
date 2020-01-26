package io.service.url.id.service;

import io.service.url.id.exception.ServerIdNotFoundException;
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
            Set<Character> idsInUse = hazelcastService.getIdsInUse();
            for (int i = 0; i < IDS.length(); i++) {
                if (!idsInUse.contains(IDS.charAt(i))) {
                    hazelcastService.saveServerIdAsUse(IDS.charAt(i));
                    log.info("Server id: {}", IDS.charAt(i));
                    return new ServerId(IDS.charAt(i));
                }
            }
            throw new ServerIdNotFoundException("No server id not in use found");
        } finally {
            lock.unlock();
        }
    }

    public void deleteServerId(char serverId) {
        log.info("Deleting {} id from use...", serverId);
        lock.lock();
        try {
            hazelcastService.deleteServerId(serverId);
            log.info("Id {} id deleted from use", serverId);
        } finally {
            lock.unlock();
        }
    }
}

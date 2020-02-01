package io.service.url.shortening.client;

import io.service.url.shortening.dto.ServerIdDto;
import io.service.url.shortening.exception.ServerIdClientException;
import org.springframework.stereotype.Component;

@Component
public class ServerIdClientFallback implements ServerIdClient {

    @Override
    public ServerIdDto getServerId() {
        throw new ServerIdClientException("Error while getting server id");
    }

    @Override
    public void deleteServerId(String id) {
        throw new ServerIdClientException("Error while deleting server id");
    }
}

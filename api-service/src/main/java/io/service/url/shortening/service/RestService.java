package io.service.url.shortening.service;

import io.service.url.shortening.client.ServerIdClient;
import io.service.url.shortening.dto.ServerIdDto;
import io.service.url.shortening.model.ServerId;
import org.springframework.stereotype.Service;

@Service
public class RestService {

    private ServerIdClient serverIdClient;

    public RestService(ServerIdClient serverIdClient) {
        this.serverIdClient = serverIdClient;
    }

    public ServerId getServerId() {
        ServerIdDto serverIdDto = serverIdClient.getServerId();
        return new ServerId(serverIdDto.getId(), serverIdDto.getFallbackId());
    }

    public void deleteServerId(String id) {
        serverIdClient.deleteServerId(id);
    }
}

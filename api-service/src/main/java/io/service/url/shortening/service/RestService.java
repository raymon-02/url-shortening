package io.service.url.shortening.service;

import io.service.url.shortening.client.ServerIdClient;
import io.service.url.shortening.model.ServerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestService {

    @Autowired
    private ServerIdClient serverIdClient;

    public ServerId getServerId() {
        return new ServerId(serverIdClient.getServerId().getId());
    }
}

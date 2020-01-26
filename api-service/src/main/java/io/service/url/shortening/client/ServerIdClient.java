package io.service.url.shortening.client;

import io.service.url.shortening.dto.ServerIdDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "id-service",
        fallback = ServerIdClientFallback.class
)
public interface ServerIdClient {

    @GetMapping("/")
    ServerIdDto getServerId();
}

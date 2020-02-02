package io.service.url.shortening.client;

import io.service.url.shortening.dto.ServerIdDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "id-service",
        fallback = ServerIdClientFallback.class
)
public interface ServerIdClient {

    @GetMapping("/")
    ServerIdDto getServerId();

    @DeleteMapping("/{id}")
    void deleteServerId(@PathVariable("id") String id);
}

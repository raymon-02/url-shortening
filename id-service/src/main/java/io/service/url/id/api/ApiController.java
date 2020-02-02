package io.service.url.id.api;

import io.service.url.id.dto.ServerIdDto;
import io.service.url.id.exception.NoAvailableServerIdException;
import io.service.url.id.model.ServerId;
import io.service.url.id.service.IdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    private IdService idService;

    @GetMapping("/")
    public ServerIdDto getServerId() {
        ServerId serverId = idService.getServerId();
        return new ServerIdDto(serverId.getId(), serverId.getFallbackId());
    }

    @DeleteMapping("/{id}")
    public void deleteServerId(@PathVariable("id") String id) {
        idService.deleteServerId(id);
    }

    @ExceptionHandler(value = NoAvailableServerIdException.class)
    public ResponseEntity<String> noAvailableServerIdHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No available server id");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> runtimeExceptionHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong");
    }
}

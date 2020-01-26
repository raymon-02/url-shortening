package io.service.url.id.api;

import io.service.url.id.dto.ServerIdDto;
import io.service.url.id.model.ServerId;
import io.service.url.id.service.IdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    private IdService idService;

    @GetMapping("/")
    public ServerIdDto getServerId() {
        ServerId serverId = idService.getServerId();
        return new ServerIdDto(serverId.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteServerId(@PathVariable("id") char serverId) {
        idService.deleteServerId(serverId);
    }

}

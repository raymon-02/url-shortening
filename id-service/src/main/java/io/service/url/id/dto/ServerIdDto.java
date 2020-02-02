package io.service.url.id.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ServerIdDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fallbackId")
    private String fallbackId;
}

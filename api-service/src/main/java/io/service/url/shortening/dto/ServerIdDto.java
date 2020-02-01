package io.service.url.shortening.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServerIdDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fallbackId")
    private String fallbackId;
}

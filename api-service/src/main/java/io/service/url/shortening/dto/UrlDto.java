package io.service.url.shortening.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UrlDto {

    @JsonProperty("url")
    private String url;

}

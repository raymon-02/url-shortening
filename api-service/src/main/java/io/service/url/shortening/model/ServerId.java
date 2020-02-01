package io.service.url.shortening.model;

import lombok.Value;

@Value
public class ServerId {
    private String id;
    private String fallbackId;
}

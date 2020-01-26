package io.service.url.shortening.exception;

public class ServerIdClientException extends RuntimeException {
    private static final long serialVersionUID = -5877686211411115903L;

    public ServerIdClientException(String message) {
        super(message);
    }
}

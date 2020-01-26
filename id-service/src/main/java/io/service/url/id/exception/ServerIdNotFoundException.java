package io.service.url.id.exception;

public class ServerIdNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 129730075447454488L;

    public ServerIdNotFoundException(String message) {
        super(message);
    }
}

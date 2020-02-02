package io.service.url.id.exception;

public class NoAvailableServerIdException extends RuntimeException {
    private static final long serialVersionUID = 129730075447454488L;

    public NoAvailableServerIdException(String message) {
        super(message);
    }
}

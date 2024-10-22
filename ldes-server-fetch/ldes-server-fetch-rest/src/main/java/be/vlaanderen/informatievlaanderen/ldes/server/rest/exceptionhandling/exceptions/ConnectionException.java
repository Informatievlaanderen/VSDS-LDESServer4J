package be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling.exceptions;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String message, Exception e) {
        super(message, e);
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception;

public class IngestValidationException extends RuntimeException {
    private final String message;

    public IngestValidationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

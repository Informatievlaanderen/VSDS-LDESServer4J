package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception;

public class IngestValidationException extends RuntimeException {

    public IngestValidationException(String message) {
        super(message);
    }
}

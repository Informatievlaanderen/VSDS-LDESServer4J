package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingEventStreamException extends RuntimeException {
    private final String collectionName;
    public MissingEventStreamException(String collectionName) {
        super();
        this.collectionName = collectionName;
    }

    @Override
    public String getMessage() {
        return "No event stream exists with collection name: " + collectionName;
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingLdesConfigException extends RuntimeException {
    private final String identifier;
    private final String objectType;

    public MissingLdesConfigException(String objectType, String identifier) {
        super();
        this.objectType = objectType;
        this.identifier = identifier;
    }

    /**
     * Constructs a new MissingLdesConfigException with default object type <i>ldes event stream</i>
     *
     * @param identifier identifier of the missing ldes config object
     */
    public MissingLdesConfigException(String identifier) {
        super();
        this.objectType = "ldes event stream";
        this.identifier = identifier;
    }

    @Override
    public String getMessage() {
        return "No " + objectType + " exists with identifier: " + identifier;
    }
}

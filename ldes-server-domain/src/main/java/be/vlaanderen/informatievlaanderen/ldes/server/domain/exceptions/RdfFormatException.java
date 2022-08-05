package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class RdfFormatException extends RuntimeException {
    String inputFormat;
    LdesProcessDirection ldesProcessDirection;

    public RdfFormatException(String inputFormat, LdesProcessDirection ldesProcessDirection) {
        super();
        this.inputFormat = inputFormat;
        this.ldesProcessDirection = ldesProcessDirection;
    }

    @Override
    public String getMessage() {
        return ldesProcessDirection + ": RDF format " + inputFormat + " is not a supported format.";
    }

    public enum LdesProcessDirection {
        INGEST, FETCH
    }

}

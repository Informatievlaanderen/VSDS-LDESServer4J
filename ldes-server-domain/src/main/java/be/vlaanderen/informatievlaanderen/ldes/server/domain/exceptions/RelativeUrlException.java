package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

import org.apache.jena.riot.Lang;

public class RelativeUrlException extends RuntimeException{
    private final String contentType;

    public RelativeUrlException(Lang lang) {
        this.contentType = lang.getContentType().getContentTypeStr();
    }

    @Override
    public String getMessage() {
        return "Content type: " + contentType + " incompatible with relative uri.";
    }
}

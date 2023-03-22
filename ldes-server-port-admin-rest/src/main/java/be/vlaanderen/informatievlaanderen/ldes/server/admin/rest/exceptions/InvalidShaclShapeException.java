package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;

public class InvalidShaclShapeException extends RuntimeException {
    public InvalidShaclShapeException(Exception e) {
        super(e);
    }
}
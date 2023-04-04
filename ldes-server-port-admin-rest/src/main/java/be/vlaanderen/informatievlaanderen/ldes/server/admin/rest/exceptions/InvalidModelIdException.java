package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;

public class InvalidModelIdException extends RuntimeException {
	private final String model;

	public InvalidModelIdException(String model) {
		this.model = model;
	}

	@Override
	public String getMessage() {
		return "Could not extract id with " + RDF_SYNTAX_TYPE + " from model: \n\n" + model;
	}
}

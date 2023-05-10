package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions;

public class ModelToViewConverterException extends RuntimeException {
	private final String cause;

	public ModelToViewConverterException(String cause) {
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return "Could not toModel model to ViewSpecification:\n" + cause;
	}
}

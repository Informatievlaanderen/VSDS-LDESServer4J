package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class LdesStreamValidationException extends RuntimeException {
	private final String collection;

	public LdesStreamValidationException(String collection) {
		super();
		this.collection = collection;
	}

	@Override
	public String getMessage() {
		return "EventStream validation failed: \n" + collection;
	}
}

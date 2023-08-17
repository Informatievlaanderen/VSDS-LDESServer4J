package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class InvalidConfigOperationException extends RuntimeException {

	private final String cause;

	public InvalidConfigOperationException(String cause) {
		super();
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return "Unable to complete operation.\nCause: " + cause;
	}
}

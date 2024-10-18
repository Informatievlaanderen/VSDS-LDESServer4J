package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions;

public class MissingStatementException extends RuntimeException {
	private final String requirement;

	public MissingStatementException(String requirement) {
		this.requirement = requirement;
	}

	@Override
	public String getMessage() {
		return "Statement could not be found. Requires: " + requirement;
	}
}

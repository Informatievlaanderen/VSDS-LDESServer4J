package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.exceptions;

public class MissingDcatServerException extends RuntimeException {
	private final String id;

	public MissingDcatServerException(String id) {
		this.id = id;
	}

	@Override
	public String getMessage() {
		return String.format("No dcat is configured on the server with id %s", id);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingServerDcatException extends RuntimeException {
	private final String id;

	public MissingServerDcatException(String id) {
		this.id = id;
	}

	@Override
	public String getMessage() {
		return String.format("No dcat is configured on the server with id %s", id);
	}
}

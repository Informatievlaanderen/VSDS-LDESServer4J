package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingEventStreamException extends RuntimeException {
	private final String collection;

	public MissingEventStreamException(String collection) {
		this.collection = collection;
	}

	@Override
	public String getMessage() {
		return "No event stream found for collection " + collection;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingLdesStreamException extends RuntimeException {
	private final String collectionName;

	public MissingLdesStreamException(String collectionName) {
		super();
		this.collectionName = collectionName;
	}

	@Override
	public String getMessage() {
		return "No event stream exists with collection name: " + collectionName;
	}
}

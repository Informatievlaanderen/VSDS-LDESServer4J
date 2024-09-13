package be.vlaanderen.informatievlaanderen.ldes.server.fetching.exceptions;

public class LdesFragmentIdentifierParseException extends RuntimeException {

	private final String id;

	public LdesFragmentIdentifierParseException(String id) {
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "LdesFragmentIdentifier could not be created from string: " + id;
	}
}

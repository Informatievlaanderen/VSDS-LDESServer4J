package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment;

public class MissingRootFragmentException extends RuntimeException {
	private final String collectionName;
	public MissingRootFragmentException(String collectionName) {
		this.collectionName = collectionName;
	}

	@Override
	public String getMessage() {
		return "Could not retrieve root fragment for collection " + collectionName;
	}
}

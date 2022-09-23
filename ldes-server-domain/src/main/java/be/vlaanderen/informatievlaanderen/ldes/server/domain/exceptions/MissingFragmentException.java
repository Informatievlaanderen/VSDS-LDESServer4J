package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingFragmentException extends RuntimeException {
	private final String fragmentId;

	public MissingFragmentException(String fragmentId) {
		super();
		this.fragmentId = fragmentId;
	}

	@Override
	public String getMessage() {
		return "No fragment exists with fragment identifier: " + fragmentId;
	}
}

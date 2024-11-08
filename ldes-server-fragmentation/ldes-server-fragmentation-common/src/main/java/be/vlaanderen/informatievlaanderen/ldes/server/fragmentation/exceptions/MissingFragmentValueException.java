package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions;

public class MissingFragmentValueException extends RuntimeException {
	private final String fragmentId;
	private final String fragmentKey;

	public MissingFragmentValueException(String fragmentId, String fragmentKey) {
		this.fragmentId = fragmentId;
		this.fragmentKey = fragmentKey;
	}

	@Override
	public String getMessage() {
		return "FragmentId " + fragmentId + "does not contain a value for fragmentkey " + fragmentKey;
	}
}

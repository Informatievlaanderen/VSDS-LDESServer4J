package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.fragmentisers;

public class FragmentiserConfigException extends RuntimeException {
	private final String cause;

	public FragmentiserConfigException(String cause) {
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return "Could not create fragmentation config: " + cause;
	}
}

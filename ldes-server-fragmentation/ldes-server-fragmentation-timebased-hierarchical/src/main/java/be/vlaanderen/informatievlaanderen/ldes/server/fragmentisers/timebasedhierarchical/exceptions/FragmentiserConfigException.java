package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.exceptions;

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

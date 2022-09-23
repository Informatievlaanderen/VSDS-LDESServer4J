package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingRootFragmentException extends RuntimeException {
	private final String viewName;

	public MissingRootFragmentException(String viewName) {
		this.viewName = viewName;
	}

	@Override
	public String getMessage() {
		return "Could not retrieve root fragment for view " + viewName;
	}
}

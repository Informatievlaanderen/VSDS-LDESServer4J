package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class DeletedFragmentException extends RuntimeException {
	private final String fragmentId;

	public DeletedFragmentException(String fragmentId) {
		super();
		this.fragmentId = fragmentId;
	}

	@Override
	public String getMessage() {
		return "Fragment with following identifier has been deleted: " + fragmentId;
	}

}

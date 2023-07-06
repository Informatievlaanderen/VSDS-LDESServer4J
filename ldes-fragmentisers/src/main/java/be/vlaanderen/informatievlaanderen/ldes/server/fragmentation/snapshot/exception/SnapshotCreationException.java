package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.exception;

public class SnapshotCreationException extends RuntimeException {

	private final String cause;

	public SnapshotCreationException(String cause) {
		super();
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return "Unable to create snapshot.\nCause: " + cause;
	}
}

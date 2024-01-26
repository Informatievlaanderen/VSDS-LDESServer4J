package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception;

public class DuplicateRetentionException extends RuntimeException {
	private final String retentionPolicy;

	public DuplicateRetentionException(String retentionPolicy) {
		this.retentionPolicy = retentionPolicy;
	}

	@Override
	public String getMessage() {
		return "More then one retention policy of type <%s> found".formatted(retentionPolicy);
	}
}

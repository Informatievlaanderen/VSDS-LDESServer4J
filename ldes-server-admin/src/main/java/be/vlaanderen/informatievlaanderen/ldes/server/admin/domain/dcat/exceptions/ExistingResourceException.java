package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.exceptions;

public class ExistingResourceException extends RuntimeException {
	private final String type;
	private final String id;

	public ExistingResourceException(String type, String id) {
		this.type = type;
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "Resource of type: " + type + " with id: " + id + " already exists.";
	}
}

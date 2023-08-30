package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.exceptions;

public class MissingResourceException extends RuntimeException {
	private final String type;
	private final String id;

	public MissingResourceException(String type, String id) {
		this.type = type;
		this.id = id;
	}

	@Override
	public String getMessage() {
		return "Resource of type: " + type + " with id: " + id + " could not be found.";
	}
}

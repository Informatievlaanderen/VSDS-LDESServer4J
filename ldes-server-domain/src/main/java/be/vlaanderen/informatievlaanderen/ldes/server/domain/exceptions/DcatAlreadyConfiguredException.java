package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class DcatAlreadyConfiguredException extends RuntimeException {
	private final String alreadyConfiguredDcatId;
	public DcatAlreadyConfiguredException(String alreadyConfiguredDcatId) {
		this.alreadyConfiguredDcatId = alreadyConfiguredDcatId;
	}

	@Override
	public String getMessage() {
		return "The server can contain only one dcat configuration and there already has been configured one with id " + alreadyConfiguredDcatId;
	}
}

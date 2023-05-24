package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class DcatAlreadyConfiguredException extends RuntimeException {
	public DcatAlreadyConfiguredException() {
		super("The server can contain only one dcat configuration and there already has been configured one");
	}
}

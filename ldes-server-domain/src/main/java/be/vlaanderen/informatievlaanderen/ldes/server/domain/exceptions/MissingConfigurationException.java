package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingConfigurationException extends RuntimeException {

	private final String configurationKey;

	public MissingConfigurationException(String configurationKey) {
		this.configurationKey = configurationKey;
	}

	@Override
	public String getMessage() {
		return "Configuration key " + configurationKey + " is missing.";
	}
}

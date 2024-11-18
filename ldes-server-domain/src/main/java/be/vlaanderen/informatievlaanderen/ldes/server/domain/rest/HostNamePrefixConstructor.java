package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import org.springframework.beans.factory.annotation.Value;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;

public class HostNamePrefixConstructor implements UriPrefixConstructor {
	private final String hostname;

	public HostNamePrefixConstructor(@Value(HOST_NAME_KEY) String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String buildPrefix() {
		return hostname;
	}


}

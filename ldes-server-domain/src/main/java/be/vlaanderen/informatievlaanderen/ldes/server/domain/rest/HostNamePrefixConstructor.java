package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

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

	public Map<String, String> buildCollectionUri(String collectionName) {
		return Map.of(collectionName, createCollectionUri(collectionName));
	}

	public Map<String, String> buildFragmentUri(String collectionName, String fragmentId) {
		String[] fragmentIdParts = fragmentId.split("[/?]");
		return Map.of(
				collectionName, createCollectionUri(collectionName),
				fragmentIdParts[2], hostname + fragmentId.split("\\?")[0] + "/"
		);
	}

	private String createCollectionUri(String collectionName) {
		return hostname + "/" + collectionName + "/";
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import java.util.Map;

public class HostNamePrefixConstructor implements UriPrefixConstructor {
	private final String hostname;

	public HostNamePrefixConstructor(String hostname) {
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
		String[] fragmentIdParts = fragmentId.split(collectionName + "/" + "|\\?");
		return Map.of(
				collectionName, createCollectionUri(collectionName),
				fragmentIdParts[1], "%s/%s/%s/".formatted(hostname, collectionName, fragmentIdParts[1])
		);
	}

	private String createCollectionUri(String collectionName) {
		return hostname + "/" + collectionName + "/";
	}
}

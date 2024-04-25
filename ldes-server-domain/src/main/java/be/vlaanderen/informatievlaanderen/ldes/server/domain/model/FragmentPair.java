package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.Map;

public record FragmentPair(String fragmentKey, String fragmentValue) {
	public static FragmentPair fromMapEntry(Map.Entry<String, String> entry) {
		return new FragmentPair(entry.getKey(), entry.getValue());
	}
}

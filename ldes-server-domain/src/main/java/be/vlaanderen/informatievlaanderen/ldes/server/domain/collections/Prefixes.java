package be.vlaanderen.informatievlaanderen.ldes.server.domain.collections;

import java.util.Map;

@FunctionalInterface
public interface Prefixes {
	Map<String, String> getPrefixes();
}

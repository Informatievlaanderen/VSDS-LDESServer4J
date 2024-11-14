package be.vlaanderen.informatievlaanderen.ldes.server.domain.collections;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "ldes-server.output")
public class ConfiguredPrefixes implements Prefixes {
	private Map<String, String> prefixes;

	@Override
	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(Map<String, String> prefixes) {
		this.prefixes = prefixes;
	}
}

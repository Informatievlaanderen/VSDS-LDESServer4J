package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingConfigurationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigProperties {
	private final Map<String, String> caseInsensitiveProperties;

	public ConfigProperties(Map<String, String> properties) {
		this.caseInsensitiveProperties = new HashMap<>();
		properties.forEach((key, value) -> caseInsensitiveProperties.put(removeCasing(key), value));
	}

	public String get(String key) {
		String caseInsensitiveKey = removeCasing(key);
		String value = caseInsensitiveProperties.get(caseInsensitiveKey);
		if (value == null)
			throw new MissingConfigurationException(key);
		return value;
	}

	public String getOrDefault(String key, String defaultValue) {
		String caseInsensitiveKey = removeCasing(key);
		return caseInsensitiveProperties.getOrDefault(caseInsensitiveKey, defaultValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ConfigProperties that = (ConfigProperties) o;
		return caseInsensitiveProperties.equals(that.caseInsensitiveProperties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseInsensitiveProperties);
	}

	private String removeCasing(String key) {
		return key
				.toLowerCase()
				.replace("-", "")
				.replace("_", "");
	}
}

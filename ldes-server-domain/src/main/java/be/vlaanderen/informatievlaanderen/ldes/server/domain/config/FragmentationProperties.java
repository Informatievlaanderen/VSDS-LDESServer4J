package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingConfigurationException;

import java.util.Map;
import java.util.Objects;

public class FragmentationProperties {
	private final Map<String, String> properties;

	public FragmentationProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String get(String key) {
		String value = properties.get(key);
		if (value == null)
			throw new MissingConfigurationException(key);
		return value;
	}

	public String getOrDefault(String key, String defaultValue) {
		return properties.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentationProperties that = (FragmentationProperties) o;
		return properties.equals(that.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(properties);
	}
}

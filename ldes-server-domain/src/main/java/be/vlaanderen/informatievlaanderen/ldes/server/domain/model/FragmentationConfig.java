package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

import java.util.Map;
import java.util.Objects;

public class FragmentationConfig {

	private String name;
	private Map<String, String> config;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	public ConfigProperties getProperties() {
		return new ConfigProperties(config);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FragmentationConfig that))
			return false;
		return Objects.equals(name, that.name) && Objects.equals(config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, config);
	}
}
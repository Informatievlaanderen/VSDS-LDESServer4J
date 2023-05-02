package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import java.util.Map;

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

}
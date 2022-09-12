package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

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

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	public FragmentationProperties getProperties() {
		return new FragmentationProperties(config);
	}

}
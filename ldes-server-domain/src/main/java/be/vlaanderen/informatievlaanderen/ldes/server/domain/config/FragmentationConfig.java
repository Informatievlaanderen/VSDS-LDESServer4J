package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "fragmentation")
public class FragmentationConfig {

	private String name;
	private Map<String, String> config;
	private FragmentationConfig fragmentation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FragmentationConfig getFragmentation() {
		return fragmentation;
	}

	public void setFragmentation(FragmentationConfig fragmentation) {
		this.fragmentation = fragmentation;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}
}
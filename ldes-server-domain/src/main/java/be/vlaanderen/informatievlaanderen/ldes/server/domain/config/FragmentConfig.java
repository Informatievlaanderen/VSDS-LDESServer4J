package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "fragmentationlist")
public class FragmentConfig {
	private List<String> fragmentations;

	public List<String> getFragmentations() {
		return fragmentations;
	}

	public void setFragmentations(List<String> fragmentations) {
		this.fragmentations = fragmentations;
	}
}

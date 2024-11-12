package be.vlaanderen.informatievlaanderen.ldes.server.domain.versioning;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppVersionConfig {

	@Bean
	public String appVersion(BuildProperties buildProperties) {
		return buildProperties.getVersion();
	}
}

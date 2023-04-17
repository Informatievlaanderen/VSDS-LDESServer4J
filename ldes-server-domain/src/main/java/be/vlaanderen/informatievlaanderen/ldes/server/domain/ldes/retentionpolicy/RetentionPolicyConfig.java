package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimebasedRetentionProperties.DURATION;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class RetentionPolicyConfig {

	@Bean
	public Map<ViewName, List<RetentionPolicy>> retentionPolicyMap(AppConfig appConfig) {
		return appConfig
				.getCollections()
				.stream()
				.flatMap(ldesSpec -> ldesSpec.getViews().stream())
				.collect(Collectors.toMap(ViewSpecification::getName,
						viewSpecification -> viewSpecification
								.getRetentionConfigs()
								.stream()
								.map(this::getRetentionPolicy)
								.toList()));
	}

	private RetentionPolicy getRetentionPolicy(RetentionConfig retentionConfig) {
		if ("timebased".equals(retentionConfig.getName())) {
			return new TimeBasedRetentionPolicy(
					retentionConfig.getProperties().get(DURATION));
		}
		throw new IllegalArgumentException("Invalid retention Policy: " + retentionConfig.getName());
	}

}

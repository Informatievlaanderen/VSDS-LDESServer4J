package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimebasedRetentionProperties.DURATION_IN_SECONDS;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class RetentionPolicyConfig {

	@Bean
	public Map<String, List<RetentionPolicy>> retentionPolicyMap(
			ViewConfig viewConfig) {
		return viewConfig
				.getViews()
				.stream()
				.collect(Collectors.toMap(ViewSpecification::getName,
						viewSpecification -> viewSpecification
								.getRetentionPolicies()
								.stream()
								.map(this::getRetentionPolicy)
								.toList()));
	}

	private RetentionPolicy getRetentionPolicy(RetentionConfig retentionConfig) {
		if ("timebased".equals(retentionConfig.getName())) {
			return new TimeBasedRetentionPolicy(
					Long.parseLong(retentionConfig.getProperties().get(DURATION_IN_SECONDS)));
		}
		throw new IllegalArgumentException("Invalid retention Policy: " + retentionConfig.getName());
	}

}

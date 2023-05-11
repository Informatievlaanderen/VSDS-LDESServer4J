package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class RetentionPolicyConfig {

	@Bean
	public Map<ViewName, List<RetentionPolicy>> retentionPolicyMap(EventStreamService eventStreamService,
			RetentionPolicyCreator retentionPolicyCreator) {

		return eventStreamService.retrieveAllEventStreams().stream()
				.flatMap(eventStream -> eventStream.getViews().stream())
				.collect(Collectors.toMap(ViewSpecification::getName,
						retentionPolicyCreator::createRetentionPolicyListForView));
	}

}

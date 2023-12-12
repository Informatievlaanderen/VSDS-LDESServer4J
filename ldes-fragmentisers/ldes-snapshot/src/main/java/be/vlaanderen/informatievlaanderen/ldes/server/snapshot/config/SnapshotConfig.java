package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SnapshotConfig {

	public static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "PaginationFragmentation";

	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");

	@Bean("snapshotFragmentation")
	public FragmentationStrategy snapshotFragmentationStrategy(ApplicationContext applicationContext,
			FragmentRepository fragmentRepository,
			ApplicationEventPublisher eventPublisher) {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = (FragmentationStrategyWrapper) applicationContext
				.getBean(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		return fragmentationStrategyWrapper.wrapFragmentationStrategy(
				applicationContext,
				new FragmentationStrategyImpl(fragmentRepository,
						eventPublisher),
				new ConfigProperties(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES));

	}
}

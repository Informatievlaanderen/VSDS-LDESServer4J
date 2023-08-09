package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_FRAGMENTATION_STRATEGY;

@Configuration
public class SnapshotConfig {

	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");

	@Bean
	@Qualifier("snapshot-fragmentation")
	public FragmentationStrategy snapshotFragmentationStrategy(ApplicationContext applicationContext,
			FragmentRepository fragmentRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor, ApplicationEventPublisher eventPublisher) {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = (FragmentationStrategyWrapper) applicationContext
				.getBean(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		return fragmentationStrategyWrapper.wrapFragmentationStrategy(
				applicationContext,
				new FragmentationStrategyImpl(fragmentRepository,
						nonCriticalTasksExecutor,
						eventPublisher),
				new ConfigProperties(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES));

	}
}

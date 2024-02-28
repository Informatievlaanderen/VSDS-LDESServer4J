package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedProperties.*;

public class HierarchicalTimeBasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		TimeBasedConfig config = createConfig(fragmentationProperties);
		TimeBasedRelationsAttributer relationsAttributer = new TimeBasedRelationsAttributer(
				fragmentRepository, config);
		TimeBasedFragmentCreator fragmentCreator = new TimeBasedFragmentCreator(fragmentRepository,
				relationsAttributer);
		TimeBasedFragmentFinder fragmentFinder = new TimeBasedFragmentFinder(fragmentCreator,
				config);
		return new HierarchicalTimeBasedFragmentationStrategy(fragmentationStrategy,
				observationRegistry, fragmentFinder,
				fragmentRepository, config);
	}

	private TimeBasedConfig createConfig(ConfigProperties properties) {
		return new TimeBasedConfig(properties.getOrDefault(FRAGMENTATION_SUBJECT_FILTER, ".*"),
				properties.get(FRAGMENTATION_PATH), Granularity.from(properties.get(MAX_GRANULARITY)),
				Boolean.parseBoolean(properties.getOrDefault(LINEAR_TIME_CACHING_ENABLED, "")));
	}

}

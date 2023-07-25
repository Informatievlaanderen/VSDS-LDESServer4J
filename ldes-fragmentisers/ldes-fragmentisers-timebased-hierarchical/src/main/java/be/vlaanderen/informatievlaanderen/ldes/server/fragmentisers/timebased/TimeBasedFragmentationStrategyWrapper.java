package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.constants.TimeBasedFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedProperties.FRAGMENTATION_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedProperties.MAX_GRANULARITY;

public class TimeBasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		TimeBasedConfig config = createConfig(fragmentationProperties);
		TimeBasedRelationsAttributer relationsAttributer = new TimeBasedRelationsAttributer(
				fragmentRepository, config);
		TimeBasedFragmentCreator fragmentCreator = new TimeBasedFragmentCreator(fragmentRepository, relationsAttributer);
		TimeBasedFragmentFinder fragmentFinder = new TimeBasedFragmentFinder(fragmentCreator,
				config, relationsAttributer);
		return new TimeBasedFragmentationStrategy(fragmentationStrategy,
				observationRegistry, fragmentFinder, fragmentCreator,
				fragmentRepository, config);
	}

	private TimeBasedConfig createConfig(ConfigProperties properties) {
		TimeBasedConfig config = new TimeBasedConfig();
		config.setFragmenterPath(properties.get(FRAGMENTATION_PATH));
		config.setMaxGranularity(properties.get(MAX_GRANULARITY));
		//config.setMemberLimit(Integer.valueOf(properties.get(MEMBER_LIMIT)));
		return config;
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.constants.TimeBasedFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.TimeBasedProperties.FRAGMENTATION_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.TimeBasedProperties.MEMBER_LIMIT;

public class TimeBasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		TimeBasedConfig config = createSubstringConfig(fragmentationProperties);
		TimeBasedFragmentCreator fragmentCreator = new TimeBasedFragmentCreator(fragmentRepository);
		TimeBasedRelationsAttributer relationsAttributer = new TimeBasedRelationsAttributer(
				fragmentRepository, config);
		TimeBasedFragmentFinder fragmentFinder = new TimeBasedFragmentFinder(fragmentCreator,
				config, relationsAttributer);
		return new TimeBasedFragmentationStrategy(fragmentationStrategy,
				observationRegistry, fragmentFinder, fragmentCreator,
				fragmentRepository, config);
	}

	private TimeBasedConfig createSubstringConfig(ConfigProperties properties) {
		TimeBasedConfig substringConfig = new TimeBasedConfig();
		substringConfig.setFragmenterPath(properties.get(FRAGMENTATION_PATH));
		substringConfig.setMemberLimit(Integer.valueOf(properties.get(MEMBER_LIMIT)));
		return substringConfig;
	}

}

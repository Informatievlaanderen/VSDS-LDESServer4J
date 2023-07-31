package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig.DEFAULT_CASE_SENSITIVE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.*;

public class SubstringFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		SubstringConfig substringConfig = createSubstringConfig(fragmentationProperties);
		SubstringFragmentCreator substringFragmentCreator = new SubstringFragmentCreator(fragmentRepository);
		SubstringRelationsAttributer substringRelationsAttributer = new SubstringRelationsAttributer(
				fragmentRepository, substringConfig);
		SubstringFragmentFinder substringFragmentFinder = new SubstringFragmentFinder(substringFragmentCreator,
				substringConfig, substringRelationsAttributer);
		return new SubstringFragmentationStrategy(fragmentationStrategy,
				observationRegistry, substringFragmentFinder, substringFragmentCreator,
				fragmentRepository, substringConfig);
	}

	private SubstringConfig createSubstringConfig(ConfigProperties properties) {
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setFragmenterPath(properties.get(FRAGMENTATION_PATH));
		substringConfig.setMemberLimit(Integer.valueOf(properties.get(MEMBER_LIMIT)));
		substringConfig.setCaseSensitive(
				Boolean.parseBoolean(properties.getOrDefault(CASE_SENSITIVE, DEFAULT_CASE_SENSITIVE)));
		return substringConfig;
	}

}

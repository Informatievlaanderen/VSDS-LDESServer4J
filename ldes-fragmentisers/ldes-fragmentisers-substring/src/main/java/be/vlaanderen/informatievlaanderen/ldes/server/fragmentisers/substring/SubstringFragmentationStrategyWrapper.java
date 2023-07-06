package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.FRAGMENTATION_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.MEMBER_LIMIT;

public class SubstringFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		SubstringConfig substringConfig = createSubstringConfig(fragmentationProperties);
		SubstringFragmentCreator substringFragmentCreator = new SubstringFragmentCreator(ldesFragmentRepository);
		SubstringRelationsAttributer substringRelationsAttributer = new SubstringRelationsAttributer(
				ldesFragmentRepository, substringConfig);
		SubstringFragmentFinder substringFragmentFinder = new SubstringFragmentFinder(substringFragmentCreator,
				substringConfig, substringRelationsAttributer);
		return new SubstringFragmentationStrategy(fragmentationStrategy,
				observationRegistry, substringFragmentFinder, substringFragmentCreator,
				ldesFragmentRepository, substringConfig);
	}

	private SubstringConfig createSubstringConfig(ConfigProperties properties) {
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setFragmenterPath(properties.get(FRAGMENTATION_PATH));
		substringConfig.setMemberLimit(Integer.valueOf(properties.get(MEMBER_LIMIT)));
		return substringConfig;
	}

}

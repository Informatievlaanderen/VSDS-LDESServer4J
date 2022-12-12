package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser.SubstringBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.FRAGMENTER_PROPERTY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.MEMBER_LIMIT;

public class SubstringFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		SubstringConfig substringConfig = createSubstringConfig(fragmentationProperties);
		SubstringBucketiser substringBucketiser = new SubstringBucketiser(substringConfig);
		SubstringFragmentCreator substringFragmentCreator = new SubstringFragmentCreator(ldesFragmentRepository);
		SubstringRelationsAttributer substringRelationsAttributer = new SubstringRelationsAttributer(
				ldesFragmentRepository, substringConfig);
		SubstringFragmentFinder substringFragmentFinder = new SubstringFragmentFinder(substringFragmentCreator,
				substringConfig, substringRelationsAttributer);
		return new SubstringFragmentationStrategy(fragmentationStrategy,
				ldesFragmentRepository,
				tracer, substringBucketiser, substringFragmentFinder, substringFragmentCreator);
	}

	private SubstringConfig createSubstringConfig(ConfigProperties properties) {
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setFragmenterPropertyQuery(properties.get(FRAGMENTER_PROPERTY));
		substringConfig.setMemberLimit(Integer.valueOf(properties.get(MEMBER_LIMIT)));
		return substringConfig;
	}

}

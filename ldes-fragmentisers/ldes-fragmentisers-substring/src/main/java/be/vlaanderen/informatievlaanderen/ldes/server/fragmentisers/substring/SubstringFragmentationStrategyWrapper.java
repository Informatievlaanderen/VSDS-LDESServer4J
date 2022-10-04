package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser.SubstringBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.relations.SubstringRelationsAttributer;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.MEMBER_LIMIT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringProperties.SUBSTRING_PROPERTY;

public class SubstringFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, FragmentationProperties properties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		SubstringConfig substringConfig = createSubstringConfig(properties);
		SubstringBucketiser substringBucketiser = new SubstringBucketiser(substringConfig);
		SubstringFragmentCreator substringFragmentCreator = new SubstringFragmentCreator(ldesFragmentRepository);
		SubstringRelationsAttributer substringRelationsAttributer = new SubstringRelationsAttributer(
				ldesFragmentRepository);
		SubstringFragmentFinder substringFragmentFinder = new SubstringFragmentFinder(substringFragmentCreator,
				substringConfig, substringRelationsAttributer);
		return new SubstringFragmentationStrategy(fragmentationStrategy,
				ldesFragmentRepository,
				tracer, substringBucketiser, substringFragmentFinder, substringFragmentCreator);
	}

	private SubstringConfig createSubstringConfig(FragmentationProperties properties) {
		SubstringConfig substringConfig = new SubstringConfig();
		substringConfig.setSubstringProperty(properties.get(SUBSTRING_PROPERTY));
		substringConfig.setMemberLimit(Integer.valueOf(properties.get(MEMBER_LIMIT)));
		return substringConfig;
	}

}

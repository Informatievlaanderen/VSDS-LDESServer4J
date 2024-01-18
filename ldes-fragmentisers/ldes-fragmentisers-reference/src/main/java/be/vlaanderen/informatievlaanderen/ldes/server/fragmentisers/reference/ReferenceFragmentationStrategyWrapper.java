package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.bucketising.ReferenceBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.config.ReferenceConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.config.ReferenceProperties.FRAGMENTATION_PROPERTY;

public class ReferenceFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		final var fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		final var observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		final var referenceConfig = createReferenceConfig(fragmentationProperties);
		final var referenceBucketiser = new ReferenceBucketiser(referenceConfig);
		final var relationsAttributer = new ReferenceFragmentRelationsAttributer(fragmentRepository);
		final var referenceFragmentCreator = new ReferenceFragmentCreator(fragmentRepository, relationsAttributer);
		return new ReferenceFragmentationStrategy(fragmentationStrategy, referenceBucketiser,
				referenceFragmentCreator, observationRegistry, fragmentRepository);
	}

	private ReferenceConfig createReferenceConfig(ConfigProperties properties) {
		return new ReferenceConfig(properties.get(FRAGMENTATION_PROPERTY));
	}

}

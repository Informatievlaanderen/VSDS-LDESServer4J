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
import org.apache.jena.vocabulary.RDF;
import org.springframework.context.ApplicationContext;


public class ReferenceFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	// TODO TVB: 23/01/24 add integration test with multi level
	public static final String FRAGMENTATION_PATH = "fragmentationPath";
	public static final String DEFAULT_FRAGMENTATION_PATH = RDF.type.getURI();

	public static final String FRAGMENTATION_KEY = "fragmentationKey";
	public static final String DEFAULT_FRAGMENTATION_KEY = "reference";

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties properties) {
		final var fragmentationPath = properties.getOrDefault(FRAGMENTATION_PATH, DEFAULT_FRAGMENTATION_PATH);
		final var fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		final var observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		final var referenceConfig = new ReferenceConfig(fragmentationPath);
		final var referenceBucketiser = new ReferenceBucketiser(referenceConfig);
		final var fragmentationKey = properties.getOrDefault(FRAGMENTATION_KEY, DEFAULT_FRAGMENTATION_KEY);
		final var relationsAttributer = new ReferenceFragmentRelationsAttributer(fragmentRepository, fragmentationPath, fragmentationKey);
		final var referenceFragmentCreator = new ReferenceFragmentCreator(fragmentRepository, relationsAttributer, fragmentationKey);
		return new ReferenceFragmentationStrategy(fragmentationStrategy, referenceBucketiser,
				referenceFragmentCreator, observationRegistry, fragmentRepository);
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.*;

public class GeospatialFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		BucketRepository bucketRepository = applicationContext.getBean(BucketRepository.class);

		GeospatialConfig geospatialConfig = createGeospatialConfig(fragmentationProperties);
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig);
		TileFragmentRelationsAttributer tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(
				fragmentRepository);
		GeospatialFragmentCreator geospatialFragmentCreator = new GeospatialFragmentCreator(fragmentRepository,
				tileFragmentRelationsAttributer);
		GeospatialBucketCreator geospatialBucketCreator = new GeospatialBucketCreator(bucketRepository, tileFragmentRelationsAttributer);

		return new GeospatialFragmentationStrategy(fragmentationStrategy,
				geospatialBucketiser, geospatialFragmentCreator, geospatialBucketCreator, observationRegistry, fragmentRepository);
	}

	private GeospatialConfig createGeospatialConfig(ConfigProperties properties) {
		return new GeospatialConfig(
				properties.getOrDefault(FRAGMENTER_SUBJECT_FILTER, ".*"),
				properties.get(FRAGMENTATION_PROPERTY),
				Integer.parseInt(properties.get(MAX_ZOOM)));
	}

}

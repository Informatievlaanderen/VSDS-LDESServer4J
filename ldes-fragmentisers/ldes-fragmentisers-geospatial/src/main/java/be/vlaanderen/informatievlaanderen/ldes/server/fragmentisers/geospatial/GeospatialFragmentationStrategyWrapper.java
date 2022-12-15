package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.FRAGMENTER_PROPERTY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.MAX_ZOOM_LEVEL;

public class GeospatialFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		GeospatialConfig geospatialConfig = createGeospatialConfig(fragmentationProperties);
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig);
		GeospatialFragmentCreator geospatialFragmentCreator = new GeospatialFragmentCreator(ldesFragmentRepository);
		TileFragmentRelationsAttributer tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(
				ldesFragmentRepository);
		return new GeospatialFragmentationStrategy(fragmentationStrategy,
				ldesFragmentRepository,
				geospatialBucketiser, geospatialFragmentCreator, tileFragmentRelationsAttributer, observationRegistry);
	}

	private GeospatialConfig createGeospatialConfig(ConfigProperties properties) {
		return new GeospatialConfig(
				properties.get(FRAGMENTER_PROPERTY),
				Integer.parseInt(properties.get(MAX_ZOOM_LEVEL)));
	}

}

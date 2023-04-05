package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.CoordinateConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.CoordinateConverterFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.Projections.NOOP;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.*;

public class GeospatialFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		GeospatialConfig geospatialConfig = createGeospatialConfig(fragmentationProperties);
		CoordinateConverter coordinateConverter = CoordinateConverterFactory
				.getCoordinateConverter(geospatialConfig.projection());
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig, coordinateConverter);
		GeospatialFragmentCreator geospatialFragmentCreator = new GeospatialFragmentCreator(ldesFragmentRepository);
		TileFragmentRelationsAttributer tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(
				ldesFragmentRepository);
		return new GeospatialFragmentationStrategy(fragmentationStrategy,
				ldesFragmentRepository,
				geospatialBucketiser, geospatialFragmentCreator, tileFragmentRelationsAttributer, tracer);
	}

	private GeospatialConfig createGeospatialConfig(ConfigProperties properties) {
		return new GeospatialConfig(
				properties.getOrDefault(FRAGMENTER_SUBJECT_FILTER, ".*"),
				properties.get(FRAGMENTER_PROPERTY),
				Integer.parseInt(properties.get(MAX_ZOOM_LEVEL)),
				properties.getOrDefault(PROJECTION, NOOP.name()));
	}

}

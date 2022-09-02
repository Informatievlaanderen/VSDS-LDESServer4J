package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.CoordinateConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.CoordinateConverterFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.*;

public class GeospatialFragmentationUpdater implements FragmentationUpdater {

	public FragmentationService updateFragmentationService(ApplicationContext applicationContext,
			FragmentationService fragmentationService, FragmentationProperties properties) {
		LdesConfig ldesConfig1 = applicationContext.getBean(LdesConfig.class);
		LdesMemberRepository ldesMemberRepository1 = applicationContext.getBean(LdesMemberRepository.class);
		LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);

		GeospatialConfig geospatialConfig = createGeospatialConfig(properties);

		CoordinateConverter coordinateConverter = CoordinateConverterFactory
				.getCoordinateConverter(geospatialConfig.getProjection());
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig, coordinateConverter);
		return new GeospatialFragmentationService(fragmentationService, ldesConfig1, ldesMemberRepository1,
				ldesFragmentRepository1,
				new GeospatialFragmentCreator(ldesConfig1), geospatialBucketiser);
	}

	private GeospatialConfig createGeospatialConfig(FragmentationProperties properties) {
		GeospatialConfig geospatialConfig = new GeospatialConfig();
		geospatialConfig.setProjection(properties.get(PROJECTION));
		geospatialConfig.setBucketiserProperty(properties.get(BUCKETISER_PROPERTY));
		geospatialConfig.setMaxZoomLevel(Integer.valueOf(properties.get(MAX_ZOOM_LEVEL)));
		return geospatialConfig;
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

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

public class GeospatialFragmentationUpdater implements FragmentationUpdater {

	public FragmentationService updateFragmentationService(ApplicationContext applicationContext,
			FragmentationService fragmentationService) {
		LdesConfig ldesConfig1 = applicationContext.getBean(LdesConfig.class);
		LdesMemberRepository ldesMemberRepository1 = applicationContext.getBean(LdesMemberRepository.class);
		LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);
		GeospatialConfig geospatialConfig = applicationContext.getBean(GeospatialConfig.class);

		CoordinateConverter coordinateConverter = CoordinateConverterFactory
				.getCoordinateConverter(geospatialConfig.getProjection());
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig, coordinateConverter);
		return new GeospatialFragmentationService(fragmentationService, ldesConfig1, ldesMemberRepository1,
				ldesFragmentRepository1,
				new GeospatialFragmentCreator(ldesConfig1), geospatialBucketiser);
	}

}

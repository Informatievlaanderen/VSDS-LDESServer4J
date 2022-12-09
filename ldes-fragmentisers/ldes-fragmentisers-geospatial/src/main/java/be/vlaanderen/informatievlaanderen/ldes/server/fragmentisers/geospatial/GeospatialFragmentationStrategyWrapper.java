package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.FRAGMENTER_PROPERTY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialProperties.MAX_ZOOM_LEVEL;

public class GeospatialFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		TreeRelationsRepository treeRelationsRepository = applicationContext
				.getBean(TreeRelationsRepository.class);
		NonCriticalTasksExecutor nonCriticalTasksExecutor = applicationContext.getBean(NonCriticalTasksExecutor.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		GeospatialConfig geospatialConfig = createGeospatialConfig(fragmentationProperties);
		GeospatialBucketiser geospatialBucketiser = new GeospatialBucketiser(geospatialConfig);
		TileFragmentRelationsAttributer tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(
				treeRelationsRepository);
		GeospatialFragmentCreator geospatialFragmentCreator = new GeospatialFragmentCreator(ldesFragmentRepository,
				tileFragmentRelationsAttributer, nonCriticalTasksExecutor);

		return new GeospatialFragmentationStrategy(fragmentationStrategy,
				geospatialBucketiser, geospatialFragmentCreator, tracer, treeRelationsRepository);
	}

	private GeospatialConfig createGeospatialConfig(ConfigProperties properties) {
		return new GeospatialConfig(
				properties.get(FRAGMENTER_PROPERTY),
				Integer.parseInt(properties.get(MAX_ZOOM_LEVEL)));
	}

}

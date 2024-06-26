package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.GeospatialFragmentationStrategy.GEOSPATIAL_FRAGMENTATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialBucketCreator {

	private final BucketRepository bucketRepository;
	private final TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialBucketCreator.class);

	public GeospatialBucketCreator(BucketRepository bucketRepository,
	                               TileFragmentRelationsAttributer tileFragmentRelationsAttributer) {
		this.bucketRepository = bucketRepository;
		this.tileFragmentRelationsAttributer = tileFragmentRelationsAttributer;
	}

	public Bucket getOrCreateTileFragment(Bucket parentBucket, String tile,
	                                        Bucket rootTileFragment) {
		Bucket child = parentBucket.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile));
		return bucketRepository
				.retrieveBucket(child.getBucketDescriptorAsString())
				.orElseGet(() -> {
					bucketRepository.insertBucket(child);
//					tileFragmentRelationsAttributer
//							.addRelationsFromRootToBottom(rootTileFragment, child);
					String viewName = parentBucket.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, GEOSPATIAL_FRAGMENTATION).increment();
					LOGGER.debug("Geospatial fragment created with id: {}", child.getBucketDescriptorAsString());
					return child;
				});
	}

	public Bucket getOrCreateRootBucket(Bucket parentBucket, String tile) {
		Bucket child = parentBucket.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile));
		return bucketRepository
				.retrieveBucket(child.getBucketDescriptorAsString())
				.orElseGet(() -> {
					bucketRepository.insertBucket(child);
					String viewName = parentBucket.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, GEOSPATIAL_FRAGMENTATION).increment();
					LOGGER.debug("Geospatial rootfragment created with id: {}", child.getBucketDescriptorAsString());
					return child;
				});
	}
}

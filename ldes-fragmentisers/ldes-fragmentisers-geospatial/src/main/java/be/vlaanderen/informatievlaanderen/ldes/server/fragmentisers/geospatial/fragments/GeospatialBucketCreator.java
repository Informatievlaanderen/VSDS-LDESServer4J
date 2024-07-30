package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileBucketRelationsAttributer;
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
	private final TileBucketRelationsAttributer tileBucketRelationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialBucketCreator.class);

	public GeospatialBucketCreator(BucketRepository bucketRepository, TileBucketRelationsAttributer tileBucketRelationsAttributer) {
		this.bucketRepository = bucketRepository;
		this.tileBucketRelationsAttributer = tileBucketRelationsAttributer;
	}

	public Bucket getOrCreateTileBucket(Bucket parentBucket, String tile, Bucket rootTileFragment) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		return bucketRepository
				.retrieveBucket(parentBucket.getViewName(), parentBucket.createChildDescriptor(childDescriptorPair))
				.orElseGet(() -> {
					final Bucket childBucket = bucketRepository.insertBucket(parentBucket.createChild(childDescriptorPair));
					tileBucketRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, childBucket);
					String viewName = parentBucket.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, GEOSPATIAL_FRAGMENTATION).increment();
					LOGGER.debug("Geospatial fragment created with id: {}", childBucket.getBucketDescriptorAsString());
					return childBucket;
				});
	}

	public Bucket getOrCreateRootBucket(Bucket parentBucket, String tile) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		return bucketRepository
				.retrieveBucket(parentBucket.getViewName(), parentBucket.createChildDescriptor(childDescriptorPair))
				.orElseGet(() -> {
					final Bucket childBucket = bucketRepository.insertBucket(parentBucket.createChild(childDescriptorPair));
					
					String viewName = parentBucket.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, GEOSPATIAL_FRAGMENTATION).increment();
					LOGGER.debug("Geospatial rootfragment created with id: {}", childBucket.getBucketDescriptorAsString());
					return childBucket;
				});
	}
}
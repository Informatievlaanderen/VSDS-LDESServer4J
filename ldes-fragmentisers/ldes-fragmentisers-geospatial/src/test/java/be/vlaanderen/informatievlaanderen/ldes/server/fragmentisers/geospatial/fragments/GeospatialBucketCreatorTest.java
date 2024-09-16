package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileBucketRelationsAttributer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeospatialBucketCreatorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timebasedPair = new BucketDescriptorPair("year", "2023");
	private static final BucketDescriptorPair geoRootPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT);
	private static final BucketDescriptorPair geoPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, "15/101/202");
	private static final BucketDescriptorPair defaultPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, DEFAULT_BUCKET_STRING);

	@Mock
	private TileBucketRelationsAttributer tileBucketRelationsAttributer;
	@InjectMocks
	private GeospatialBucketCreator geospatialBucketCreator;

	@Test
	void test_GetOrCreateTileBucket() {
		Bucket rootBucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootTileBucket = rootBucket.createChild(geoRootPair);
		Bucket tileBucket = rootBucket.createChild(geoPair);

		Bucket childBucket = geospatialBucketCreator.getOrCreateTileBucket(rootBucket, "15/101/202", rootTileBucket);

		assertThat(childBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&tile=15/101/202");
		verify(tileBucketRelationsAttributer).addRelationsFromRootToBottom(rootTileBucket, tileBucket);
	}

	@Test
	void test_GetOrCreateRootBucket() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateRootBucket(bucket, FRAGMENT_KEY_TILE_ROOT);

		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&tile=0/0/0");
		verifyNoInteractions(tileBucketRelationsAttributer);
	}

	@Test
	void test_GetOrCreateDefaultTileBucket() {
		Bucket rootbucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootTileBucket = rootbucket.createChild(geoRootPair);
		Bucket defaultBucket = rootbucket.createChild(defaultPair);

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateTileBucket(rootbucket, DEFAULT_BUCKET_STRING, rootTileBucket);

		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&tile=unknown");
		verify(tileBucketRelationsAttributer).addRelationsFromRootToBottom(rootTileBucket, defaultBucket);
	}
}
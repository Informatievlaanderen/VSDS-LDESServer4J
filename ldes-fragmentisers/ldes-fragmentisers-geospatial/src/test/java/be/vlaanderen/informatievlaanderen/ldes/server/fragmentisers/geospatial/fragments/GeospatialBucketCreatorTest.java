package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

	private BucketRepository bucketRepository;
	private GeospatialBucketCreator geospatialBucketCreator;

	@BeforeEach
	void setUp() {
		bucketRepository = mock(BucketRepository.class);
		geospatialBucketCreator = new GeospatialBucketCreator(bucketRepository, mock());
	}

	@Test
	void when_TileFragmentDoesNotExist_NewTileFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootBucket = bucket.createChild(geoRootPair);
		Bucket childBucket = bucket.createChild(geoPair);

		when(bucketRepository.retrieveBucket(VIEW_NAME, childBucket.getBucketDescriptor()))
				.thenReturn(Optional.empty());
		when(bucketRepository.insertBucket(any())).thenReturn(childBucket);

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateTileBucket(bucket, "15/101/202", rootBucket);

		assertThat(returnedBucket)
				.describedAs("Child instance must be the same, to assure the bucket instance from the db is returned")
				.isSameAs(childBucket);
		verify(bucketRepository).retrieveBucket(VIEW_NAME, childBucket.getBucketDescriptor());
	}

	@Test
	void when_TileFragmentDoesNotExist_RetrievedTileFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootBucket = bucket.createChild(geoRootPair);
		Bucket tileBucket = bucket.createChild(geoPair);

		when(bucketRepository.retrieveBucket(VIEW_NAME, tileBucket.getBucketDescriptor()))
				.thenReturn(Optional.of(tileBucket));

		Bucket childBucket = geospatialBucketCreator.getOrCreateTileBucket(bucket, "15/101/202", rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&tile=15/101/202");
		verify(bucketRepository).retrieveBucket(VIEW_NAME, tileBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootBucket = bucket.createChild(geoRootPair);
		when(bucketRepository.retrieveBucket(VIEW_NAME, rootBucket.getBucketDescriptor())).thenReturn(Optional.empty());
		when(bucketRepository.insertBucket(any())).thenReturn(rootBucket);

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateRootBucket(bucket, FRAGMENT_KEY_TILE_ROOT);

		assertThat(returnedBucket)
				.describedAs("Child instance must be the same, to assure the bucket instance from the db is returned")
				.isSameAs(rootBucket);
		verify(bucketRepository).retrieveBucket(VIEW_NAME, rootBucket.getBucketDescriptor());
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootBucket = bucket.createChild(geoRootPair);
		when(bucketRepository.retrieveBucket(VIEW_NAME, rootBucket.getBucketDescriptor()))
				.thenReturn(Optional.of(rootBucket));

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateRootBucket(bucket, FRAGMENT_KEY_TILE_ROOT);
		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&tile=0/0/0");
		verify(bucketRepository).retrieveBucket(VIEW_NAME, rootBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_DefaultFragmentDoesNotExist_NewDefaultFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootBucket = bucket.createChild(geoRootPair);
		Bucket defaultBucket = bucket.createChild(defaultPair);
		when(bucketRepository.retrieveBucket(VIEW_NAME, defaultBucket.getBucketDescriptor())).thenReturn(Optional.empty());
		when(bucketRepository.insertBucket(any())).thenReturn(defaultBucket);

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateTileBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(returnedBucket)
				.describedAs("Child instance must be the same, to assure the bucket instance from the db is returned")
				.isSameAs(defaultBucket);
		assertThat(returnedBucket.getBucketDescriptor()).isEqualTo(defaultBucket.getBucketDescriptor());
		verify(bucketRepository).retrieveBucket(VIEW_NAME, defaultBucket.getBucketDescriptor());
	}

	@Test
	void when_DefaultFragmentDoesNotExist_RetrievedDefaultFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), VIEW_NAME);
		Bucket rootBucket = bucket.createChild(geoRootPair);
		Bucket defaultBucket = bucket.createChild(defaultPair);
		when(bucketRepository.retrieveBucket(VIEW_NAME, defaultBucket.getBucketDescriptor()))
				.thenReturn(Optional.of(defaultBucket));

		Bucket returnedBucket = geospatialBucketCreator.getOrCreateTileBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&tile=unknown");
		verify(bucketRepository).retrieveBucket(VIEW_NAME, defaultBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}
}
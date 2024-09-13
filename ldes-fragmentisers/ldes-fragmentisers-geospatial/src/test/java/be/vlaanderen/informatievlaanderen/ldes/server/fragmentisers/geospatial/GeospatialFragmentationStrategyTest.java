package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GeospatialFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

	private Bucket parentBucket;
	private Bucket rootTileBucket;
	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialBucketCreator bucketCreator;
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		parentBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		rootTileBucket = parentBucket.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

		geospatialBucketiser = mock(GeospatialBucketiser.class);
		bucketCreator = mock(GeospatialBucketCreator.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_TILE_ROOT)).thenReturn(rootTileBucket);
		geospatialFragmentationStrategy = new GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				geospatialBucketiser, bucketCreator, ObservationRegistry.create());
	}

	@Test
	void when_RootTileBucketIsNotSet_then_SetRootTileBucket() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.createTiles(member.getSubject(), member.getVersionModel())).thenReturn(Set.of("dummy"));
		when(bucketCreator.getOrCreateTileBucket(parentBucket, "dummy", parentBucket))
				.thenReturn(mock(Bucket.class));

		geospatialFragmentationStrategy.addMemberToBucket(parentBucket, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucket(any(), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
		assertThat(parentBucket.getChildren())
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(rootTileBucket.withGenericRelation());
	}

	@Test
	void when_MemberIsAddedToDefaultFragment_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.createTiles(member.getSubject(), member.getVersionModel())).thenReturn(Set.of(DEFAULT_BUCKET_STRING));
		Bucket defaultTileBucket = parentBucket.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, DEFAULT_BUCKET_STRING));
		when(bucketCreator.getOrCreateTileBucket(parentBucket, DEFAULT_BUCKET_STRING, parentBucket))
				.thenReturn(defaultTileBucket);

		geospatialFragmentationStrategy.addMemberToBucket(parentBucket, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(defaultTileBucket), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	@Test
	void when_MemberIsAddedToBucket_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.createTiles(member.getSubject(), member.getVersionModel())).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		Bucket tileBucketOne = mockCreationGeospatialBucket("1/1/1");
		Bucket tileBucketTwo = mockCreationGeospatialBucket("2/2/2");
		Bucket tileBucketThree = mockCreationGeospatialBucket("3/3/3");

		geospatialFragmentationStrategy.addMemberToBucket(parentBucket, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(tileBucketOne), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(tileBucketTwo), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(tileBucketThree), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private Bucket mockCreationGeospatialBucket(String tile) {
		Bucket tileBucket = parentBucket.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile));
		when(bucketCreator.getOrCreateTileBucket(parentBucket, tile, rootTileBucket)).thenReturn(tileBucket);
		return tileBucket;
	}
}

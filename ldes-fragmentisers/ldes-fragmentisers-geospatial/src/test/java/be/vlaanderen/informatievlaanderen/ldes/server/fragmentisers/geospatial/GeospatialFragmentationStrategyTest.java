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
import static org.mockito.Mockito.*;

class GeospatialFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Bucket PARENT_BUCKET = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final Bucket ROOT_TILE_BUCKET = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialBucketCreator bucketCreator;
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		geospatialBucketiser = mock(GeospatialBucketiser.class);
		bucketCreator = mock(GeospatialBucketCreator.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(bucketCreator.getOrCreateRootBucket(PARENT_BUCKET, FRAGMENT_KEY_TILE_ROOT)).thenReturn(ROOT_TILE_BUCKET);
		geospatialFragmentationStrategy = new GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				geospatialBucketiser, bucketCreator, ObservationRegistry.create(),
				mock());
	}
	
	@Test
	void when_MemberIsAddedToDefaultFragment_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.getSubject(), member.getVersionModel())).thenReturn(Set.of(DEFAULT_BUCKET_STRING));
		Bucket defaultTileBucket = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, DEFAULT_BUCKET_STRING));
		when(bucketCreator.getOrCreateTileBucket(PARENT_BUCKET, DEFAULT_BUCKET_STRING, PARENT_BUCKET))
				.thenReturn(defaultTileBucket);

		geospatialFragmentationStrategy.addMemberToBucketAndReturnMembers(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToBucketAndReturnMembers(eq(defaultTileBucket),
				any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	@Test
	void when_MemberIsAddedToBucket_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.getSubject(), member.getVersionModel())).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		Bucket tileBucketOne = mockCreationGeospatialBucket("1/1/1");
		Bucket tileBucketTwo = mockCreationGeospatialBucket("2/2/2");
		Bucket tileBucketThree = mockCreationGeospatialBucket("3/3/3");

		geospatialFragmentationStrategy.addMemberToBucketAndReturnMembers(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(tileBucketOne), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(tileBucketTwo), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(tileBucketThree), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	@Test
	void when_MemberIsAddedToDefaultBucket_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.getSubject(), member.getVersionModel())).thenReturn(Set.of(DEFAULT_BUCKET_STRING));
		Bucket defaultTileBucket = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, DEFAULT_BUCKET_STRING));
		when(bucketCreator.getOrCreateTileBucket(PARENT_BUCKET, DEFAULT_BUCKET_STRING, PARENT_BUCKET))
				.thenReturn(defaultTileBucket);

		geospatialFragmentationStrategy.addMemberToBucketAndReturnMembers(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucketAndReturnMembers(eq(defaultTileBucket), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private Bucket mockCreationGeospatialBucket(String tile) {
		Bucket tileBucket = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile));
		when(bucketCreator.getOrCreateTileBucket(PARENT_BUCKET, tile, ROOT_TILE_BUCKET)).thenReturn(tileBucket);
		return tileBucket;
	}
}

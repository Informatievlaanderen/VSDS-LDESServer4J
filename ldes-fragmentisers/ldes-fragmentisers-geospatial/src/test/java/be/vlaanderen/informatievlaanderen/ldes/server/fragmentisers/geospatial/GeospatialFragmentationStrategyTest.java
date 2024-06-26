package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.mockito.Mockito.*;

class GeospatialFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Fragment PARENT_FRAGMENT = new Fragment(
			new LdesFragmentIdentifier(VIEW_NAME, List.of()));
	private static final Bucket PARENT_BUCKET = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final Bucket ROOT_TILE_BUCKET = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));
	private static final Fragment ROOT_TILE_FRAGMENT = PARENT_FRAGMENT
			.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialFragmentCreator fragmentCreator;
	private GeospatialBucketCreator bucketCreator;
	private final FragmentRepository treeRelationsRepository = mock(FragmentRepository.class);
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		geospatialBucketiser = mock(GeospatialBucketiser.class);
		fragmentCreator = mock(GeospatialFragmentCreator.class);
		bucketCreator = mock(GeospatialBucketCreator.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentCreator.getOrCreateRootFragment(PARENT_FRAGMENT,
				FRAGMENT_KEY_TILE_ROOT))
				.thenReturn(ROOT_TILE_FRAGMENT);
		when(bucketCreator.getOrCreateRootBucket(PARENT_BUCKET, FRAGMENT_KEY_TILE_ROOT)).thenReturn(ROOT_TILE_BUCKET);
		geospatialFragmentationStrategy = new GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				geospatialBucketiser, fragmentCreator, bucketCreator, ObservationRegistry.create(),
				treeRelationsRepository);
	}

	@Test
	void when_MemberIsAddedToFragment_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.id(), member.model())).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		Fragment tileFragmentOne = mockCreationGeospatialFragment("1/1/1");
		Fragment tileFragmentTwo = mockCreationGeospatialFragment("2/2/2");
		Fragment tileFragmentThree = mockCreationGeospatialFragment("3/3/3");

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentOne),
						any(), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentTwo),
						any(), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentThree),
						any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}
	@Test
	void when_MemberIsAddedToDefaultFragment_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.id(), member.model())).thenReturn(Set.of(DEFAULT_BUCKET_STRING));
		Fragment defaultTileFragment = PARENT_FRAGMENT.createChild(new FragmentPair(FRAGMENT_KEY_TILE, DEFAULT_BUCKET_STRING));
		when(fragmentCreator.getOrCreateTileFragment(PARENT_FRAGMENT, DEFAULT_BUCKET_STRING,
				PARENT_FRAGMENT))
				.thenReturn(defaultTileFragment);

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(defaultTileFragment),
				any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	@Test
	void when_MemberIsAddedToBucket_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.id(), member.model())).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		Bucket tileBucketOne = mockCreationGeospatialBucket("1/1/1");
		Bucket tileBucketTwo = mockCreationGeospatialBucket("2/2/2");
		Bucket tileBucketThree = mockCreationGeospatialBucket("3/3/3");

		geospatialFragmentationStrategy.addMemberToBucket(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(tileBucketOne), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(tileBucketTwo), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(tileBucketThree), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	@Test
	void when_MemberIsAddedToDefaultBucket_GeospatialFragmentationIsApplied() {
		FragmentationMember member = mock(FragmentationMember.class);

		when(geospatialBucketiser.bucketise(member.id(), member.model())).thenReturn(Set.of(DEFAULT_BUCKET_STRING));
		Bucket defaultTileBucket = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, DEFAULT_BUCKET_STRING));
		when(bucketCreator.getOrCreateTileFragment(PARENT_BUCKET, DEFAULT_BUCKET_STRING, PARENT_BUCKET))
				.thenReturn(defaultTileBucket);

		geospatialFragmentationStrategy.addMemberToBucket(PARENT_BUCKET, member, mock(Observation.class));

		verify(decoratedFragmentationStrategy).addMemberToBucket(eq(defaultTileBucket), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private Fragment mockCreationGeospatialFragment(String tile) {
		Fragment tileFragment = PARENT_FRAGMENT.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		when(fragmentCreator.getOrCreateTileFragment(PARENT_FRAGMENT, tile,
				ROOT_TILE_FRAGMENT))
				.thenReturn(tileFragment);
		return tileFragment;
	}

	private Bucket mockCreationGeospatialBucket(String tile) {
		Bucket tileBucket = PARENT_BUCKET.createChild(new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile));
		when(bucketCreator.getOrCreateTileFragment(PARENT_BUCKET, tile, ROOT_TILE_BUCKET)).thenReturn(tileBucket);
		return tileBucket;
	}
}

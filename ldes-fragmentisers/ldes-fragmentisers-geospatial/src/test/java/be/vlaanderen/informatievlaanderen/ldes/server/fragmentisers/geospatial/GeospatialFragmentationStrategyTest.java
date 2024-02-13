package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.mockito.Mockito.*;

class GeospatialFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Fragment PARENT_FRAGMENT = new Fragment(
			new LdesFragmentIdentifier(VIEW_NAME, List.of()));
	private static final Fragment ROOT_TILE_FRAGMENT = PARENT_FRAGMENT
			.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialFragmentCreator fragmentCreator;
	private final FragmentRepository treeRelationsRepository = mock(FragmentRepository.class);
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		geospatialBucketiser = mock(GeospatialBucketiser.class);
		fragmentCreator = mock(GeospatialFragmentCreator.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentCreator.getOrCreateRootFragment(PARENT_FRAGMENT,
				FRAGMENT_KEY_TILE_ROOT))
				.thenReturn(ROOT_TILE_FRAGMENT);
		geospatialFragmentationStrategy = new GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				geospatialBucketiser, fragmentCreator, ObservationRegistry.create(),
				treeRelationsRepository);
	}

	@Test
	void when_MemberIsAddedToFragment_GeospatialFragmentationIsApplied() {
		Member member = mock(Member.class);

		when(geospatialBucketiser.bucketise(member.id(), member.model())).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		Fragment tileFragmentOne = mockCreationGeospatialFragment("1/1/1");
		Fragment tileFragmentTwo = mockCreationGeospatialFragment("2/2/2");
		Fragment tileFragmentThree = mockCreationGeospatialFragment("3/3/3");

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member.id(),
				member.model(), mock(Observation.class));

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentOne),
						any(), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentTwo),
						any(), any(), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentThree),
						any(), any(), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private Fragment mockCreationGeospatialFragment(String tile) {
		Fragment tileFragment = PARENT_FRAGMENT.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		when(fragmentCreator.getOrCreateTileFragment(PARENT_FRAGMENT, tile,
				ROOT_TILE_FRAGMENT))
				.thenReturn(tileFragment);
		return tileFragment;
	}
}

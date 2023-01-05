package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
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

	private static final String VIEW_NAME = "view";
	private static final LdesFragment PARENT_FRAGMENT = new LdesFragment(new
			FragmentInfo(VIEW_NAME, List.of()));
	private static final LdesFragment ROOT_TILE_FRAGMENT = PARENT_FRAGMENT
			.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialFragmentCreator fragmentCreator;
	private ObservationRegistry observationRegistry;
	private final TreeRelationsRepository treeRelationsRepository =
			mock(TreeRelationsRepository.class);
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		geospatialBucketiser = mock(GeospatialBucketiser.class);
		fragmentCreator = mock(GeospatialFragmentCreator.class);
		observationRegistry = ObservationRegistry.create();
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentCreator.getOrCreateRootFragment(PARENT_FRAGMENT,
				FRAGMENT_KEY_TILE_ROOT))
				.thenReturn(ROOT_TILE_FRAGMENT);
		geospatialFragmentationStrategy = new
				GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				geospatialBucketiser, fragmentCreator, observationRegistry,
				treeRelationsRepository);
	}

	@Test
	void when_MemberIsAddedToFragment_GeospatialFragmentationIsApplied() {
		Member member = mock(Member.class);

		when(geospatialBucketiser.bucketise(member)).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		LdesFragment tileFragmentOne = mockCreationGeospatialFragment("1/1/1");
		LdesFragment tileFragmentTwo = mockCreationGeospatialFragment("2/2/2");
		LdesFragment tileFragmentThree = mockCreationGeospatialFragment("3/3/3");

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member,
				mock(Observation.class));

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentOne),
				eq(member), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentTwo),
				eq(member), any(Observation.class));
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(tileFragmentThree),
				eq(member), any(Observation.class));
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
	}

	private LdesFragment mockCreationGeospatialFragment(String tile) {
		LdesFragment tileFragment = PARENT_FRAGMENT.createChild(new
				FragmentPair(FRAGMENT_KEY_TILE, tile));
		when(fragmentCreator.getOrCreateTileFragment(PARENT_FRAGMENT, tile,
				ROOT_TILE_FRAGMENT))
				.thenReturn(tileFragment);
		return tileFragment;
	}
}
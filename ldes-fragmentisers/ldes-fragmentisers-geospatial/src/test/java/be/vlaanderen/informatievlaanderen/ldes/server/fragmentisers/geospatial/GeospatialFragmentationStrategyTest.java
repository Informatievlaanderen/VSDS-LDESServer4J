package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.mockito.Mockito.*;

class GeospatialFragmentationStrategyTest {

	private static final String VIEW_NAME = "view";
	private static final LdesFragment PARENT_FRAGMENT = new LdesFragment(new FragmentInfo(VIEW_NAME, List.of()));
	private static final LdesFragment ROOT_TILE_FRAGMENT = PARENT_FRAGMENT
			.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialFragmentCreator fragmentCreator;
	private Tracer tracer;
	private final TreeNodeRelationsRepository treeNodeRelationsRepository = mock(TreeNodeRelationsRepository.class);
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		geospatialBucketiser = mock(GeospatialBucketiser.class);
		fragmentCreator = mock(GeospatialFragmentCreator.class);
		tracer = mock(Tracer.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		when(fragmentCreator.getOrCreateRootFragment(PARENT_FRAGMENT, FRAGMENT_KEY_TILE_ROOT))
				.thenReturn(ROOT_TILE_FRAGMENT);
		geospatialFragmentationStrategy = new GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				geospatialBucketiser, fragmentCreator, tracer, treeNodeRelationsRepository);
	}

	@Test
	void when_MemberIsAddedToFragment_GeospatialFragmentationIsApplied() {
		Member member = mock(Member.class);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("geospatial fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);
		when(geospatialBucketiser.bucketise(member)).thenReturn(Set.of("1/1/1",
				"2/2/2", "3/3/3"));
		LdesFragment tileFragmentOne = mockCreationGeospatialFragment("1/1/1");
		LdesFragment tileFragmentTwo = mockCreationGeospatialFragment("2/2/2");
		LdesFragment tileFragmentThree = mockCreationGeospatialFragment("3/3/3");

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member,
				parentSpan);

		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(tileFragmentOne,
						member, childSpan);
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(tileFragmentTwo,
						member, childSpan);
		verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(tileFragmentThree,
						member, childSpan);
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
		verify(childSpan, times(1)).end();
	}

	private LdesFragment mockCreationGeospatialFragment(String tile) {
		LdesFragment tileFragment = PARENT_FRAGMENT.createChild(new FragmentPair(FRAGMENT_KEY_TILE, tile));
		when(fragmentCreator.getOrCreateTileFragment(PARENT_FRAGMENT, tile,
				ROOT_TILE_FRAGMENT))
				.thenReturn(tileFragment);
		return tileFragment;
	}
}
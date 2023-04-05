package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class GeospatialFragmentationStrategyTest {

	private static final String VIEW_NAME = "view";
	private static LdesFragment PARENT_FRAGMENT;
	private GeospatialBucketiser geospatialBucketiser;
	private GeospatialFragmentCreator fragmentCreator;
	private TileFragmentRelationsAttributer tileFragmentRelationsAttributer;
	private Tracer tracer;
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private FragmentationStrategy decoratedFragmentationStrategy;
	private GeospatialFragmentationStrategy geospatialFragmentationStrategy;

	@BeforeEach
	void setUp() {
		geospatialBucketiser = mock(GeospatialBucketiser.class);
		fragmentCreator = mock(GeospatialFragmentCreator.class);
		tileFragmentRelationsAttributer = mock(
				TileFragmentRelationsAttributer.class);
		tracer = mock(Tracer.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		PARENT_FRAGMENT = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		geospatialFragmentationStrategy = new GeospatialFragmentationStrategy(decoratedFragmentationStrategy,
				ldesFragmentRepository, geospatialBucketiser, fragmentCreator, tileFragmentRelationsAttributer, tracer);
	}

	@Test
	void when_TileFragmentsAreCreated_RelationsAreAttributedAndDecoratedServiceIsCalled() {
		Member member = mock(Member.class);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("geospatial fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);
		when(geospatialBucketiser.bucketise(member)).thenReturn(Set.of("1/1/1", "2/2/2", "3/3/3"));
		TileFragment tileFragmentOne = mockCreationGeospatialFragment("1/1/1", true);
		TileFragment tileFragmentTwo = mockCreationGeospatialFragment("2/2/2", false);
		TileFragment tileFragmentThree = mockCreationGeospatialFragment("3/3/3", true);
		TileFragment rootTileFragment = mockCreationGeospatialFragment("0/0/0", false);
		when(tileFragmentRelationsAttributer.addRelationsFromRootToBottom(eq(rootTileFragment.ldesFragment()),
				anyList()))
				.thenReturn(Stream.of(tileFragmentOne.ldesFragment(), tileFragmentTwo.ldesFragment(),
						tileFragmentThree.ldesFragment()));

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member, parentSpan);

		verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(tileFragmentOne.ldesFragment(),
				member, childSpan);
		verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(tileFragmentTwo.ldesFragment(),
				member, childSpan);
		verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(tileFragmentThree.ldesFragment(),
				member, childSpan);
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
		verify(childSpan, times(1)).end();
	}

	@Test
	void when_TileFragmentsExist_DecoratedServiceIsCalled() {
		Member member = mock(Member.class);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("geospatial fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);
		when(geospatialBucketiser.bucketise(member)).thenReturn(Set.of("1/1/1", "2/2/2", "3/3/3"));
		TileFragment tileFragmentOne = mockCreationGeospatialFragment("1/1/1", false);
		TileFragment tileFragmentTwo = mockCreationGeospatialFragment("2/2/2", false);
		TileFragment tileFragmentThree = mockCreationGeospatialFragment("3/3/3", false);

		geospatialFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member, parentSpan);

		verifyNoInteractions(tileFragmentRelationsAttributer);
		verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(tileFragmentOne.ldesFragment(),
				member, childSpan);
		verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(tileFragmentTwo.ldesFragment(),
				member, childSpan);
		verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(tileFragmentThree.ldesFragment(),
				member, childSpan);
		verifyNoMoreInteractions(decoratedFragmentationStrategy);
		verify(childSpan, times(1)).end();
	}

	private TileFragment mockCreationGeospatialFragment(String tile, boolean created) {
		TileFragment tileFragment = new TileFragment(PARENT_FRAGMENT.createChild(new FragmentPair("tile", tile)),
				created);
		when(fragmentCreator.getOrCreateGeospatialFragment(PARENT_FRAGMENT, tile)).thenReturn(tileFragment);
		return tileFragment;
	}
}
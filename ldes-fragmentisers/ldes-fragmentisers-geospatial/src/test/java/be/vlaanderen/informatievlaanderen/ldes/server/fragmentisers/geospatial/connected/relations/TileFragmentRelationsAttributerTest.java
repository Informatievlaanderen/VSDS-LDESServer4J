package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TileFragmentRelationsAttributerTest {

	private static LdesFragment PARENT_FRAGMENT;
	private static final String VIEW_NAME = "view";
	private TileFragmentRelationsAttributer tileFragmentRelationsAttributer;

	private LdesFragmentRepository ldesFragmentRepository;

	@BeforeEach
	void setUp() {
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		PARENT_FRAGMENT = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(treeNodeRelationsRepository);
	}

	@Test
	void when_TileFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
		TileFragment rootFragment = createTileFragment("0/0/0", false);
		TileFragment tileFragmentOne = createTileFragment("1/1/1", true);
		TileFragment tileFragmentTwo = createTileFragment("2/2/2", true);
		TileFragment tileFragmentThree = createTileFragment("3/3/3", false);

		Stream<LdesFragment> ldesFragments = tileFragmentRelationsAttributer.addRelationsFromRootToBottom(
				rootFragment.ldesFragment(), List.of(tileFragmentOne, tileFragmentTwo, tileFragmentThree));

		assertEquals(2, rootFragment.ldesFragment().getRelations().size());
		assertEquals(List.of(tileFragmentOne.ldesFragment(), tileFragmentTwo.ldesFragment(),
				tileFragmentThree.ldesFragment()), ldesFragments.collect(Collectors.toList()));
		verify(ldesFragmentRepository, times(1)).saveFragment(rootFragment.ldesFragment());
	}

	private TileFragment createTileFragment(String tile, boolean created) {
		return new TileFragment(PARENT_FRAGMENT.createChild(new FragmentPair("tile", tile)),
				created);
	}

}
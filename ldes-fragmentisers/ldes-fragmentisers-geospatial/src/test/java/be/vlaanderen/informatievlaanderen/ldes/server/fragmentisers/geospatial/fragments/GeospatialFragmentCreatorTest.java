package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GeospatialFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

	private LdesFragmentRepository ldesFragmentRepository;
	private GeospatialFragmentCreator geospatialFragmentCreator;

	@BeforeEach
	void setUp() {
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		TileFragmentRelationsAttributer tileFragmentRelationsAttributer = mock(TileFragmentRelationsAttributer.class);
		geospatialFragmentCreator = new GeospatialFragmentCreator(ldesFragmentRepository,
				tileFragmentRelationsAttributer);
	}

	@Test
	void when_TileFragmentDoesNotExist_NewTileFragmentIsCreatedAndSaved() {
		LdesFragment ldesFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair("substring", "a")));
		LdesFragment rootFragment = ldesFragment
				.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));
		String tileFragmentId = ldesFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, "15/101/202"))
				.getFragmentId();

		when(ldesFragmentRepository.retrieveFragment(tileFragmentId)).thenReturn(Optional.empty());

		LdesFragment childFragment = geospatialFragmentCreator.getOrCreateTileFragment(ldesFragment,
				"15/101/202", rootFragment);

		assertEquals("/collectionName/view?substring=a&tile=15/101/202",
				childFragment.getFragmentId());
		verify(ldesFragmentRepository,
				times(1)).retrieveFragment(tileFragmentId);
		verify(ldesFragmentRepository,
				times(1)).saveFragment(childFragment);
	}

	@Test
	void when_TileFragmentDoesNotExist_RetrievedTileFragmentIsReturned() {
		LdesFragment ldesFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair("substring", "a")));
		LdesFragment rootFragment = ldesFragment
				.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));
		LdesFragment tileFragment = ldesFragment.createChild(new FragmentPair(FRAGMENT_KEY_TILE, "15/101/202"));

		when(ldesFragmentRepository.retrieveFragment(tileFragment.getFragmentId()))
				.thenReturn(Optional.of(tileFragment));

		LdesFragment childFragment = geospatialFragmentCreator.getOrCreateTileFragment(ldesFragment,
				"15/101/202", rootFragment);

		assertEquals("/collectionName/view?substring=a&tile=15/101/202",
				childFragment.getFragmentId());
		verify(ldesFragmentRepository,
				times(1)).retrieveFragment(tileFragment.getFragmentId());
		verifyNoMoreInteractions(ldesFragmentRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		LdesFragment ldesFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair("substring", "a")));
		LdesFragment rootFragment = ldesFragment
				.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));

		when(ldesFragmentRepository.retrieveFragment(rootFragment.getFragmentId())).thenReturn(Optional.empty());

		LdesFragment returnedFragment = geospatialFragmentCreator.getOrCreateRootFragment(ldesFragment,
				FRAGMENT_KEY_TILE_ROOT);

		assertEquals("/collectionName/view?substring=a&tile=0/0/0", returnedFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).saveFragment(returnedFragment);
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		LdesFragment ldesFragment = new LdesFragment(
				VIEW_NAME, List.of(new FragmentPair("substring", "a")));
		LdesFragment rootFragment = ldesFragment
				.createChild(new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT));
		when(ldesFragmentRepository.retrieveFragment(rootFragment.getFragmentId()))
				.thenReturn(Optional.of(rootFragment));

		LdesFragment returnedFragment = geospatialFragmentCreator.getOrCreateTileFragment(ldesFragment,
				FRAGMENT_KEY_TILE_ROOT, rootFragment);

		assertEquals("/collectionName/view?substring=a&tile=0/0/0", returnedFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
		verifyNoMoreInteractions(ldesFragmentRepository);
	}
}
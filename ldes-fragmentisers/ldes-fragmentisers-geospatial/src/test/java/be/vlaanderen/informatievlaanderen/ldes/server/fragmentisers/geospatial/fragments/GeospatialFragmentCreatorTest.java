package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileFragmentRelationsAttributer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GeospatialFragmentCreatorTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final FragmentPair timebasedPair = new FragmentPair("year", "2023");
	private static final FragmentPair geoRootPair = new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT);
	private static final FragmentPair geoPair = new FragmentPair(FRAGMENT_KEY_TILE, "15/101/202");

	private FragmentRepository fragmentRepository;
	private GeospatialFragmentCreator geospatialFragmentCreator;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		TileFragmentRelationsAttributer tileFragmentRelationsAttributer = mock(TileFragmentRelationsAttributer.class);
		geospatialFragmentCreator = new GeospatialFragmentCreator(fragmentRepository,
				tileFragmentRelationsAttributer);
	}

	@Test
	void when_TileFragmentDoesNotExist_NewTileFragmentIsCreatedAndSaved() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(timebasedPair)));
		Fragment rootFragment = fragment
				.createChild(geoRootPair);
		LdesFragmentIdentifier tileFragmentId = fragment
				.createChild(geoPair)
				.getFragmentId();

		when(fragmentRepository.retrieveFragment(tileFragmentId)).thenReturn(Optional.empty());

		Fragment childFragment = geospatialFragmentCreator.getOrCreateTileFragment(fragment,
				"15/101/202", rootFragment);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timebasedPair, geoPair)),
				childFragment.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(tileFragmentId);
		verify(fragmentRepository,
				times(1)).saveFragment(childFragment);
	}

	@Test
	void when_TileFragmentDoesNotExist_RetrievedTileFragmentIsReturned() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(timebasedPair)));
		Fragment rootFragment = fragment
				.createChild(geoRootPair);
		Fragment tileFragment = fragment.createChild(geoPair);

		when(fragmentRepository.retrieveFragment(tileFragment.getFragmentId()))
				.thenReturn(Optional.of(tileFragment));

		Fragment childFragment = geospatialFragmentCreator.getOrCreateTileFragment(fragment,
				"15/101/202", rootFragment);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timebasedPair, geoPair)),
				childFragment.getFragmentId());
		verify(fragmentRepository,
				times(1)).retrieveFragment(tileFragment.getFragmentId());
		verifyNoMoreInteractions(fragmentRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(timebasedPair)));
		Fragment rootFragment = fragment
				.createChild(geoRootPair);

		when(fragmentRepository.retrieveFragment(rootFragment.getFragmentId())).thenReturn(Optional.empty());

		Fragment returnedFragment = geospatialFragmentCreator.getOrCreateRootFragment(fragment,
				FRAGMENT_KEY_TILE_ROOT);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timebasedPair, geoRootPair)),
				returnedFragment.getFragmentId());
		verify(fragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
		verify(fragmentRepository, times(1)).saveFragment(returnedFragment);
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(timebasedPair)));
		Fragment rootFragment = fragment
				.createChild(geoRootPair);
		when(fragmentRepository.retrieveFragment(rootFragment.getFragmentId()))
				.thenReturn(Optional.of(rootFragment));

		Fragment returnedFragment = geospatialFragmentCreator.getOrCreateTileFragment(fragment,
				FRAGMENT_KEY_TILE_ROOT, rootFragment);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(timebasedPair, geoRootPair)),
				returnedFragment.getFragmentId());
		verify(fragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
		verifyNoMoreInteractions(fragmentRepository);
	}
}

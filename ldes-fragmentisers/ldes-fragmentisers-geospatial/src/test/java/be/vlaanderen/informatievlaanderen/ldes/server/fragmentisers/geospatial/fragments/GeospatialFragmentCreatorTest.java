package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
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
	private static final FragmentPair substringPair = new FragmentPair("substring", "a");
	private static final FragmentPair geoRootPair = new FragmentPair(FRAGMENT_KEY_TILE, FRAGMENT_KEY_TILE_ROOT);
	private static final FragmentPair geoPair = new FragmentPair(FRAGMENT_KEY_TILE, "15/101/202");

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
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(substringPair)));
		LdesFragment rootFragment = ldesFragment
				.createChild(geoRootPair);
		LdesFragmentIdentifier tileFragmentId = ldesFragment
				.createChild(geoPair)
				.getFragmentId();

		when(ldesFragmentRepository.retrieveFragment(tileFragmentId)).thenReturn(Optional.empty());

		LdesFragment childFragment = geospatialFragmentCreator.getOrCreateTileFragment(ldesFragment,
				"15/101/202", rootFragment);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(substringPair, geoPair)),
				childFragment.getFragmentId());
		verify(ldesFragmentRepository,
				times(1)).retrieveFragment(tileFragmentId);
		verify(ldesFragmentRepository,
				times(1)).saveFragment(childFragment);
	}

	@Test
	void when_TileFragmentDoesNotExist_RetrievedTileFragmentIsReturned() {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(substringPair)));
		LdesFragment rootFragment = ldesFragment
				.createChild(geoRootPair);
		LdesFragment tileFragment = ldesFragment.createChild(geoPair);

		when(ldesFragmentRepository.retrieveFragment(tileFragment.getFragmentId()))
				.thenReturn(Optional.of(tileFragment));

		LdesFragment childFragment = geospatialFragmentCreator.getOrCreateTileFragment(ldesFragment,
				"15/101/202", rootFragment);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(substringPair, geoPair)),
				childFragment.getFragmentId());
		verify(ldesFragmentRepository,
				times(1)).retrieveFragment(tileFragment.getFragmentId());
		verifyNoMoreInteractions(ldesFragmentRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(substringPair)));
		LdesFragment rootFragment = ldesFragment
				.createChild(geoRootPair);

		when(ldesFragmentRepository.retrieveFragment(rootFragment.getFragmentId())).thenReturn(Optional.empty());

		LdesFragment returnedFragment = geospatialFragmentCreator.getOrCreateRootFragment(ldesFragment,
				FRAGMENT_KEY_TILE_ROOT);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(substringPair, geoRootPair)),
				returnedFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).saveFragment(returnedFragment);
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(substringPair)));
		LdesFragment rootFragment = ldesFragment
				.createChild(geoRootPair);
		when(ldesFragmentRepository.retrieveFragment(rootFragment.getFragmentId()))
				.thenReturn(Optional.of(rootFragment));

		LdesFragment returnedFragment = geospatialFragmentCreator.getOrCreateTileFragment(ldesFragment,
				FRAGMENT_KEY_TILE_ROOT, rootFragment);

		assertEquals(new LdesFragmentIdentifier(VIEW_NAME, List.of(substringPair, geoRootPair)),
				returnedFragment.getFragmentId());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(rootFragment.getFragmentId());
		verifyNoMoreInteractions(ldesFragmentRepository);
	}
}
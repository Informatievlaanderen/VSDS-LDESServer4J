package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TileFragmentRelationsAttributerTest {

	private static Fragment PARENT_FRAGMENT;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private TileFragmentRelationsAttributer tileFragmentRelationsAttributer;

	private FragmentRepository fragmentRepository;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(fragmentRepository);
	}

	@Test
	void when_TileFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
		Fragment rootFragment = createTileFragment("0/0/0");
		Fragment tileFragment = createTileFragment("1/1/1");
		TreeRelation expectedRelation = new TreeRelation("http://www.opengis.net/ont/geosparql#asWKT",
				LdesFragmentIdentifier.fromFragmentId("/collectionName/view?tile=1/1/1"),
				"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POLYGON ((180 0, 180 -85.0511287798066, 0 -85.0511287798066, 0 0, 180 0))",
				"http://www.opengis.net/ont/geosparql#wktLiteral",
				"https://w3id.org/tree#GeospatiallyContainsRelation");

		tileFragmentRelationsAttributer.addRelationsFromRootToBottom(
				rootFragment, tileFragment);

		assertTrue(rootFragment.containsRelation(expectedRelation));
		verify(fragmentRepository,
				times(1)).saveFragment(rootFragment);
	}

	private Fragment createTileFragment(String tile) {
		return PARENT_FRAGMENT.createChild(new FragmentPair(FRAGMENT_KEY_TILE,
				tile));
	}

}

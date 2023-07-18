package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TileFragmentRelationsAttributerTest {

	private static LdesFragment PARENT_FRAGMENT;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private TileFragmentRelationsAttributer tileFragmentRelationsAttributer;

	private LdesFragmentRepository ldesFragmentRepository;

	@BeforeEach
	void setUp() {
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		PARENT_FRAGMENT = new LdesFragment(VIEW_NAME, List.of());
		tileFragmentRelationsAttributer = new TileFragmentRelationsAttributer(ldesFragmentRepository);
	}

	@Test
	void when_TileFragmentsAreCreated_RelationsBetweenRootAndCreatedFragmentsAreAdded() {
		LdesFragment rootFragment = createTileFragment("0/0/0");
		LdesFragment tileFragment = createTileFragment("1/1/1");
		TreeRelation expectedRelation = new TreeRelation("http://www.opengis.net/ont/geosparql#asWKT",
				"/collectionName/view?tile=1/1/1",
				"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POLYGON ((180 0, 180 -85.0511287798066, 0 -85.0511287798066, 0 0, 180 0))",
				"http://www.opengis.net/ont/geosparql#wktLiteral",
				"https://w3id.org/tree#GeospatiallyContainsRelation");

		tileFragmentRelationsAttributer.addRelationsFromRootToBottom(
				rootFragment, tileFragment);

		assertTrue(rootFragment.containsRelation(expectedRelation));
		verify(ldesFragmentRepository,
				times(1)).saveFragment(rootFragment);
	}

	private LdesFragment createTileFragment(String tile) {
		return PARENT_FRAGMENT.createChild(new FragmentPair(FRAGMENT_KEY_TILE,
				tile));
	}

}
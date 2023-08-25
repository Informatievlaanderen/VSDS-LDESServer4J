package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GeospatialRelationsAttributerTest {

	private final GeospatialRelationsAttributer geospatialRelationsAttributer = new GeospatialRelationsAttributer();
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Fragment CHILD_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
			List.of(new FragmentPair(FRAGMENT_KEY_TILE,
					"1/1/1"))));

	private static final TreeRelation EXPECTED_RELATION = new TreeRelation("http://www.opengis.net/ont/geosparql#asWKT",
			LdesFragmentIdentifier.fromFragmentId("/collectionName/view?tile=1/1/1"),
			"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POLYGON ((180 0, 180 -85.0511287798066, 0 -85.0511287798066, 0 0, 180 0))",
			"http://www.opengis.net/ont/geosparql#wktLiteral",
			"https://w3id.org/tree#GeospatiallyContainsRelation");

	@Test
	void when_getRelation_GeospatialRelationIsReturned() {
		TreeRelation relationToParentFragment = geospatialRelationsAttributer.getRelationToParentFragment(
				CHILD_FRAGMENT);
		assertEquals(EXPECTED_RELATION, relationToParentFragment);
	}
}

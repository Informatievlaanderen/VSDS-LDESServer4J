// package
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// import java.util.List;
//
// import static
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// class GeospatialRelationsAttributerTest {
//
// private final GeospatialRelationsAttributer geospatialRelationsAttributer =
// new GeospatialRelationsAttributer();
// private static LdesFragment PARENT_FRAGMENT;
// private static LdesFragment CHILD_FRAGMENT;
// private static final String VIEW_NAME = "view";
//
// private static final TreeRelation EXPECTED_RELATION = new
// TreeRelation("http://www.opengis.net/ont/geosparql#asWKT",
// "/view?tile=1/1/1",
// "<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POLYGON ((180 0, 180
// -85.0511287798066, 0 -85.0511287798066, 0 0, 180 0))",
// "http://www.opengis.net/ont/geosparql#wktLiteral",
// "https://w3id.org/tree#GeospatiallyContainsRelation");
//
// @BeforeEach
// void setUp() {
// PARENT_FRAGMENT = new LdesFragment(
// new FragmentInfo(VIEW_NAME, List.of()));
// CHILD_FRAGMENT = new LdesFragment(
// new FragmentInfo(VIEW_NAME, List.of(new FragmentPair(FRAGMENT_KEY_TILE,
// "1/1/1"))));
// }
//
// @Test
// void when_ParentHasNotYetRelation_AddRelation() {
// geospatialRelationsAttributer.getRelationToParentFragment(PARENT_FRAGMENT,
// CHILD_FRAGMENT);
// assertTrue(CHILD_FRAGMENT.getRelations().isEmpty());
// assertEquals(List.of(EXPECTED_RELATION), PARENT_FRAGMENT.getRelations());
// }
//
// @Test
// void when_ParentHasAlreadyRelation_DoNotAddRelation() {
// PARENT_FRAGMENT.addRelation(EXPECTED_RELATION);
//
// geospatialRelationsAttributer.getRelationToParentFragment(PARENT_FRAGMENT,
// CHILD_FRAGMENT);
// assertTrue(CHILD_FRAGMENT.getRelations().isEmpty());
// assertEquals(List.of(EXPECTED_RELATION), PARENT_FRAGMENT.getRelations());
// }
//
// }
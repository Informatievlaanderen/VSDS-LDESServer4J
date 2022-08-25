// package
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.GeospatialRelationsAttributer;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
//
// import java.util.List;
// import java.util.Set;
// import java.util.stream.Collectors;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
// TODO fix
// class ConnectedFragmentsFinderTest {
//
// private final static String FRAGMENT_ID = "15/4/4";
// private final LdesFragmentRepository ldesFragmentRepository =
// mock(LdesFragmentRepository.class);
// private final GeospatialRelationsAttributer relationsAttributer = new
// GeospatialRelationsAttributer();
// private final FragmentGenerator fragmentGenerator =
// mock(FragmentGenerator.class);
//
// ConnectedFragmentsFinder connectedFragmentsFinder = new
// ConnectedFragmentsFinder(ldesFragmentRepository,
// relationsAttributer, fragmentGenerator, geospatialConfig);
//
// @Test
// @DisplayName("ConnectedFragmentsFinder returns given fragment")
// void when_NoOtherFragmentsExist_MethodReturnsGivenFragment() {
// LdesFragment ldesFragment = getLdesFragment(FRAGMENT_ID);
// when(ldesFragmentRepository.retrieveAllFragments()).thenReturn(List.of());
//
// List<LdesFragment> connectedFragments =
// connectedFragmentsFinder.findConnectedFragments(ldesFragment);
//
// assertEquals(List.of(ldesFragment), connectedFragments);
//
// }
//
// @Test
// @DisplayName("ConnectedFragmentsFinder returns all connected fragments with
// updated relations")
// void
// when_OtherFragmentsExist_MethodReturnsAllConnectedFragmentsToClosestFragment()
// {
// LdesFragment ldesFragment = getLdesFragment(FRAGMENT_ID);
// String closestFragmentId = "15/3/2";
// List<String> connectedFragmentids = List.of("15/4/3", "15/4/2");
// List<String> availableFragmentsIds = List.of(closestFragmentId, "15/10/10");
// List<LdesFragment> availableFragments =
// availableFragmentsIds.stream().map(this::getLdesFragment).toList();
// when(ldesFragmentRepository.retrieveAllFragments()).thenReturn(availableFragments);
// when(fragmentGenerator.generateFragmentPathToClosestFragment(ldesFragment,
// availableFragments,
// fragmentPairList))
// .thenReturn(List.of(ldesFragment,
// getLdesFragment(connectedFragmentids.get(0)),
// getLdesFragment(connectedFragmentids.get(1)), availableFragments.get(0)));
//
// List<LdesFragment> connectedFragments =
// connectedFragmentsFinder.findConnectedFragments(ldesFragment);
//
// assertEquals(4, connectedFragments.size());
// assertEquals(Set.of(FRAGMENT_ID, connectedFragmentids.get(0),
// connectedFragmentids.get(1), closestFragmentId),
// connectedFragments.stream().map(LdesFragment::getFragmentId).collect(Collectors.toSet()));
// assertTrue(connectedFragments.stream().allMatch(ldesFragment1 ->
// ldesFragment1.getRelations().size() >= 1));
// }
//
// private LdesFragment getLdesFragment(String fragmentValue) {
// return new LdesFragment(fragmentValue,
// new FragmentInfo("", List.of(new
// FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, fragmentValue))));
// }
//
// }
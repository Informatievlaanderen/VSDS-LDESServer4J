// package
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileFragment;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// import java.util.List;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// class GeospatialFragmentCreatorTest {
//
// private LdesFragmentRepository ldesFragmentRepository;
// private GeospatialFragmentCreator geospatialFragmentCreator;
//
// @BeforeEach
// void setUp() {
// ldesFragmentRepository = mock(LdesFragmentRepository.class);
// geospatialFragmentCreator = new
// GeospatialFragmentCreator(ldesFragmentRepository,
// tileFragmentRelationsAttributer, nonCriticalTasksExecutor);
// }
//
// @Test
// void when_FragmentDoesNotExist_NewTileFragmentIsCreated() {
// LdesFragment ldesFragment = new LdesFragment(
// new FragmentInfo("view", List.of(new FragmentPair("substring", "a"))));
// LdesFragmentRequest ldesFragmentRequest = new
// LdesFragmentRequest(ldesFragment.getFragmentInfo().getViewName(),
// List.of(new FragmentPair("substring", "a"), new FragmentPair("tile",
// "15/101/202")));
// when(ldesFragmentRepository.retrieveFragment(ldesFragmentRequest)).thenReturn(Optional.empty());
//
// TileFragment childFragment =
// geospatialFragmentCreator.getOrCreateGeospatialFragment(ldesFragment,
// "15/101/202", rootTileFragment);
//
// assertEquals("/view?substring=a&tile=15/101/202",
// childFragment.ldesFragment().getFragmentId());
// assertTrue(childFragment.created());
// verify(ldesFragmentRepository,
// times(1)).retrieveFragment(ldesFragmentRequest);
// verifyNoMoreInteractions(ldesFragmentRepository);
// }
//
// @Test
// void when_FragmentExists_RetrievedFragmentIsReturned() {
// LdesFragment ldesFragment = new LdesFragment(
// new FragmentInfo("view", List.of(new FragmentPair("substring", "a"))));
// LdesFragmentRequest ldesFragmentRequest = new
// LdesFragmentRequest(ldesFragment.getFragmentInfo().getViewName(),
// List.of(new FragmentPair("substring", "a"), new FragmentPair("tile",
// "15/101/202")));
// when(ldesFragmentRepository.retrieveFragment(ldesFragmentRequest))
// .thenReturn(Optional.of(ldesFragment.createChild(new FragmentPair("tile",
// "15/101/202"))));
//
// TileFragment childFragment =
// geospatialFragmentCreator.getOrCreateGeospatialFragment(ldesFragment,
// "15/101/202", rootTileFragment);
//
// assertEquals("/view?substring=a&tile=15/101/202",
// childFragment.ldesFragment().getFragmentId());
// assertFalse(childFragment.created());
// verify(ldesFragmentRepository,
// times(1)).retrieveFragment(ldesFragmentRequest);
// verifyNoMoreInteractions(ldesFragmentRepository);
// }
// }
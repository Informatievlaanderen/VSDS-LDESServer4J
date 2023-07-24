// package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
// import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
// import io.micrometer.observation.Observation;
// import io.micrometer.observation.ObservationRegistry;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
//
// import java.util.List;
//
// import static org.mockito.Mockito.*;
//
// class RefragmentationServiceImplTest {
// public static final String COLLECTION_NAME = "collection";
// public static final String VIEW = "view";
// private final EventSourceService eventSourceService =
// mock(EventSourceService.class);
// private final FragmentationStrategy fragmentationStrategy =
// Mockito.mock(FragmentationStrategy.class);
//
// private final RefragmentationService refragmentationService = new
// RefragmentationServiceImpl(
// rootFragmentCreator, eventSourceService, ObservationRegistry.create());
//
// TODO TVB: 24/07/23 test
// @Test
// void test() {
// List<Member> members = List.of(getMember("1"), getMember("2"),
// getMember("3"));
// when(eventSourceService.getMemberStreamOfCollection(COLLECTION_NAME))
// .thenReturn(members.stream());
// Fragment parentFragment = new Fragment(
// new LdesFragmentIdentifier(new ViewName(COLLECTION_NAME, VIEW), List.of()));
//
// refragmentationService.refragmentMembersForView(parentFragment,
// fragmentationStrategy);
//
// members.forEach(member -> verify(fragmentationStrategy)
// .addMemberToFragment(eq(parentFragment), eq(member.getId()),
// eq(member.getModel()),
// any(Observation.class)));
// }
//
// private Member getMember(String memberId) {
// return new Member(memberId, null, null, null);
// }
//
// }

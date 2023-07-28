package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class RefragmentationServiceImplTest {

	public static final String COLLECTION_NAME = "collection";
	public static final String VIEW = "view";

	private final EventSourceService eventSourceService = mock(EventSourceService.class);
	private final FragmentationStrategyExecutor fragmentationStrategyExecutor = mock(FragmentationStrategyExecutor.class);
	private final RootFragmentCreator rootFragmentCreator = mock(RootFragmentCreator.class);
	private final MemberToFragmentRepository memberToFragmentRepository = mock(MemberToFragmentRepository.class);

	private final RefragmentationService refragmentationService = new RefragmentationServiceImpl(
			eventSourceService, memberToFragmentRepository);

	@Test
	void test() {
		List<Member> members = List.of(getMember("1"), getMember("2"),
				getMember("3"));
		when(eventSourceService.getMemberStreamOfCollection(COLLECTION_NAME))
				.thenReturn(members.stream());
		ViewName viewName = new ViewName(COLLECTION_NAME, VIEW);
		Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of()));
		when(rootFragmentCreator.createRootFragmentForView(viewName)).thenReturn(parentFragment);

		refragmentationService.refragmentMembersForView(viewName, fragmentationStrategyExecutor);

//		members.forEach(member -> verify(fragmentationStrategyExecutor)
//				.addMemberToFragment(eq(parentFragment), eq(member.getId()),
//						eq(member.getModel()),
//						any(Observation.class)));
	}

	private Member getMember(String memberId) {
		return new Member(memberId, null, null, null);
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

class RefragmentationServiceImplTest {
	public static final String COLLECTION_NAME = "collection";
	public static final String VIEW = "view";
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final FragmentationStrategy fragmentationStrategy = Mockito.mock(FragmentationStrategy.class);

	private final RefragmentationService refragmentationService = new RefragmentationServiceImpl(memberRepository,
			ObservationRegistry.create());

	@Test
	void test() {
		List<Member> members = List.of(getMember("1"), getMember("2"), getMember("3"));
		when(memberRepository.getMemberStreamOfCollection(COLLECTION_NAME))
				.thenReturn(members.stream());
		LdesFragment parentFragment = new LdesFragment(
				new LdesFragmentIdentifier(new ViewName(COLLECTION_NAME, VIEW), List.of()));

		refragmentationService.refragmentMembersForView(parentFragment, fragmentationStrategy);

		members.forEach(member -> verify(fragmentationStrategy)
				.addMemberToFragment(eq(parentFragment), eq(member.getLdesMemberId()), eq(member.getModel()),
				any(Observation.class)));
	}

	private Member getMember(String memberId) {
		return new Member(memberId, null, null, null, null, null, null);
	}

}

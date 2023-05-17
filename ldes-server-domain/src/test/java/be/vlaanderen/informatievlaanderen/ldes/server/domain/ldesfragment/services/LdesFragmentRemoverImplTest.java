package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class LdesFragmentRemoverImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);

	private final LdesFragmentRemover ldesFragmentRemover = new LdesFragmentRemoverImpl(ldesFragmentRepository,
			memberRepository);

	@Test
	void when_LdesFragmentOfViewAreRemoved_TheyAreRemovedFromRepository() {
		ViewName viewName = new ViewName("collection", "view");
		when(ldesFragmentRepository.retrieveFragmentsOfView(viewName.asString())).thenReturn(ldesFragmentStream());
		when(memberRepository.getMembersByReference(getLdesFragment("1").getFragmentId()))
				.thenReturn(Stream.of(getMember("1"), getMember("2")));
		when(memberRepository.getMembersByReference(getLdesFragment("2").getFragmentId()))
				.thenReturn(Stream.of(getMember("2")));

		ldesFragmentRemover.removeLdesFragmentsOfView(viewName);

		InOrder inOrder = inOrder(memberRepository, ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository).retrieveFragmentsOfView(viewName.asString());
		inOrder.verify(memberRepository).getMembersByReference(getLdesFragment("1").getFragmentId());
		inOrder.verify(memberRepository).removeMemberReference(getMember("1").getLdesMemberId(),
				getLdesFragment("1").getFragmentId());
		inOrder.verify(memberRepository).removeMemberReference(getMember("2").getLdesMemberId(),
				getLdesFragment("1").getFragmentId());
		inOrder.verify(memberRepository).getMembersByReference(getLdesFragment("2").getFragmentId());
		inOrder.verify(memberRepository).removeMemberReference(getMember("2").getLdesMemberId(),
				getLdesFragment("2").getFragmentId());
		inOrder.verify(ldesFragmentRepository).removeLdesFragmentsOfView(viewName.asString());
		inOrder.verifyNoMoreInteractions();
	}

	private Member getMember(String memberId) {
		return new Member(memberId, null, null, null, null, null, null);
	}

	private Stream<LdesFragment> ldesFragmentStream() {
		LdesFragment firstLdesFragment = getLdesFragment("1");
		LdesFragment secondLdesFragment = getLdesFragment("2");
		return Stream.of(firstLdesFragment, secondLdesFragment);
	}

	private LdesFragment getLdesFragment(String fragmentValue) {
		LdesFragment firstLdesFragment = new LdesFragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "view"),
						List.of(new FragmentPair("page", fragmentValue))));
		return firstLdesFragment;
	}

}
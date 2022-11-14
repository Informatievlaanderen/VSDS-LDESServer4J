package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TreeMemberRemoverImplTest {

	private final MemberReferencesRepository memberReferencesRepository = mock(MemberReferencesRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private TreeMemberRemover treeMemberRemover;

	@BeforeEach
	void setUp() {
		treeMemberRemover = new TreeMemberRemoverImpl(memberReferencesRepository, memberRepository);
	}

	@Test
	void when_memberHasNoReferences_ItCanBeDeleted() {
		when(memberReferencesRepository.hasMemberReferences("memberId")).thenReturn(false);
		treeMemberRemover.tryRemovingMember("memberId");

		verify(memberRepository, times(1)).deleteMember("memberId");
	}

	@Test
	void when_memberHasReferences_ItCanNotBeDeleted() {
		when(memberReferencesRepository.hasMemberReferences("memberId")).thenReturn(true);
		treeMemberRemover.tryRemovingMember("memberId");

		verifyNoInteractions(memberRepository);
	}

}
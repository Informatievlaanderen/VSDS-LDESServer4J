package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TreeMemberRemoverImplTest {

	private final MemberReferencesRepository memberReferencesRepository = mock(MemberReferencesRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private TreeMemberRemover treeMemberRemover;

	@BeforeEach
	void setUp() {
		treeMemberRemover = new TreeMemberRemoverImpl(memberRepository);
	}

	@Test
	void when_memberHasNoReferences_ItCanBeDeleted() {
		when(memberRepository.deleteMember("memberId")).thenReturn(true);
		boolean memberDeleted = treeMemberRemover.tryRemovingMember("memberId");

		assertTrue(memberDeleted);
		verify(memberRepository, times(1)).deleteMember("memberId");
	}

	@Test
	void when_memberHasReferences_ItCanNotBeDeleted() {
		when(memberRepository.deleteMember("memberId")).thenReturn(false);
		boolean memberDeleted = treeMemberRemover.tryRemovingMember("memberId");

		assertFalse(memberDeleted);
		verify(memberRepository, times(1)).deleteMember("memberId");
	}

}
package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class TreeMemberRemoverImplTest {

	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private TreeMemberRemover treeMemberRemover;

	@BeforeEach
	void setUp() {
		treeMemberRemover = new TreeMemberRemoverImpl(memberRepository);
	}

	@Test
	void when_memberHasNoReferences_ItCanBeDeleted() {
		when(memberRepository.getMember("memberId")).thenReturn(Optional.of(new Member("memberId", "collectionName", 0L, null, null, null, List.of())));
		treeMemberRemover.deletingMemberFromCollection("memberId");

		verify(memberRepository, times(1)).getMember("memberId");
		verify(memberRepository, times(1)).deleteMember("memberId");
		verifyNoMoreInteractions(memberRepository);
	}

	@Test
	void when_memberHasReferences_ItCannotBeDeleted() {
		when(memberRepository.getMember("memberId"))
				.thenReturn(Optional.of(new Member("memberId", "collectionName", 0L, null, null, null, List.of("reference"))));
		treeMemberRemover.deletingMemberFromCollection("memberId");

		verify(memberRepository, times(1)).getMember("memberId");
		verifyNoMoreInteractions(memberRepository);
	}

	@Test
	void when_memberDoesNotExistAnymore_ItCannotBeDeleted() {
		when(memberRepository.getMember("memberId")).thenReturn(Optional.empty());
		treeMemberRemover.deletingMemberFromCollection("memberId");

		verify(memberRepository, times(1)).getMember("memberId");
		verifyNoMoreInteractions(memberRepository);
	}

}
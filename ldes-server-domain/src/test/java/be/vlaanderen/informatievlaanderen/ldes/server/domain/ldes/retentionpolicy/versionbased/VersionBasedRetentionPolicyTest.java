package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionBasedRetentionPolicyTest {

	@Mock
	private MemberRepository memberRepository;

	private VersionBasedRetentionPolicy versionBasedRetentionPolicy;

	@BeforeEach
	void setUp() {
		versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(2,
				memberRepository);
	}

	@Test
	void when_TimestampIsNull_then_VersionBasedRetentionPolicyReturnsFalse() {
		Member member = getMember("id", "1", null);

		boolean memberMatchesPolicy = versionBasedRetentionPolicy.matchesPolicy(member);

		assertFalse(memberMatchesPolicy);
	}

	@Test
	void when_VersionOfIsNull_then_VersionBasedRetentionPolicyReturnsFalse() {
		Member member = getMember("id", null, LocalDateTime.now());

		boolean memberMatchesPolicy = versionBasedRetentionPolicy.matchesPolicy(member);

		assertFalse(memberMatchesPolicy);
	}

	@Test
	void when_MultipleVersionsOfAResource_then_VersionBasedRetentionPolicyReturnsTrueForNMostRecentMembers() {
		Member member1 = getMember("1/1", "1", LocalDateTime.now().plusMinutes(1));
		Member member2 = getMember("1/2", "1", LocalDateTime.now().plusMinutes(2));
		Member member3 = getMember("1/3", "1", LocalDateTime.now().plusMinutes(3));
		Member member4 = getMember("1/4", "1", LocalDateTime.now().plusMinutes(4));

		when(memberRepository.getMembersOfVersion("1")).thenReturn(List.of(member2, member3, member1, member4));

		assertTrue(versionBasedRetentionPolicy.matchesPolicy(member1));
		assertTrue(versionBasedRetentionPolicy.matchesPolicy(member2));
		assertFalse(versionBasedRetentionPolicy.matchesPolicy(member3));
		assertFalse(versionBasedRetentionPolicy.matchesPolicy(member4));
	}

	private Member getMember(String memberId, String versionOf, LocalDateTime timestamp) {
		return new Member(memberId, null, null, versionOf, timestamp, null, null);
	}

}
package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
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
	private MemberPropertiesRepository memberPropertiesRepository;

	private VersionBasedRetentionPolicy versionBasedRetentionPolicy;

	@BeforeEach
	void setUp() {
		versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(2,
				memberPropertiesRepository);
	}

	@Test
	void when_TimestampIsNull_then_VersionBasedRetentionPolicyReturnsFalse() {
		MemberProperties memberProperties = getMemberProperties("id", "1", null);

		boolean memberMatchesPolicy = versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties, viewName);

		assertFalse(memberMatchesPolicy);
	}

	@Test
	void when_VersionOfIsNull_then_VersionBasedRetentionPolicyReturnsFalse() {
		MemberProperties memberProperties = getMemberProperties("id", null, LocalDateTime.now());

		boolean memberMatchesPolicy = versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties, viewName);

		assertFalse(memberMatchesPolicy);
	}

	@Test
	void when_LessMembersThanToKeep_then_VersionBasedRetentionPolicyReturnsFalse() {
		MemberProperties memberProperties = getMemberProperties("id", "1", LocalDateTime.now());
		when(memberPropertiesRepository.getMemberPropertiesOfVersionAndView("1", viewName)).thenReturn(List.of(memberProperties));

		boolean memberMatchesPolicy = versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties, viewName);

		assertFalse(memberMatchesPolicy);
	}

	@Test
	void when_MultipleVersionsOfAResource_then_VersionBasedRetentionPolicyReturnsTrueForNMostRecentMembers() {
		MemberProperties memberProperties1 = getMemberProperties("1/1", "1", LocalDateTime.now().plusMinutes(1));
		MemberProperties memberProperties2 = getMemberProperties("1/2", "1", LocalDateTime.now().plusMinutes(2));
		MemberProperties memberProperties3 = getMemberProperties("1/3", "1", LocalDateTime.now().plusMinutes(3));
		MemberProperties memberProperties4 = getMemberProperties("1/4", "1", LocalDateTime.now().plusMinutes(4));

		when(memberPropertiesRepository.getMemberPropertiesOfVersionAndView("1", viewName))
				.thenReturn(List.of(memberProperties2, memberProperties3, memberProperties1, memberProperties4));

		assertTrue(versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties1, viewName));
		assertTrue(versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties2, viewName));
		assertFalse(versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties3, viewName));
		assertFalse(versionBasedRetentionPolicy.matchesPolicyOfView(memberProperties4, viewName));
	}

	private MemberProperties getMemberProperties(String memberId, String versionOf, LocalDateTime timestamp) {
		return new MemberProperties(memberId, null, versionOf, timestamp);
	}

}
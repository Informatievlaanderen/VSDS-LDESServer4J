package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.pointintime;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointInTimeRetentionPolicyTest {

	private final RetentionPolicy retentionPolicy = new PointInTimeRetentionPolicy(
			LocalDateTime.of(2020, 1, 15, 4, 30));

	@Test
	void when_TimestampOfMemberIsNull_then_ItDoesNotMatchThePointInTimeRetentionPolicy() {
		Member member = new Member("id", null, null, null, null, null, null);

		assertFalse(retentionPolicy.matchesPolicy(member));
	}

	@Test
	void when_TimestampOfMemberBeforePointInTime_then_ItMatchesThePointInTimeRetentionPolicy() {
		LocalDateTime beforeTimestamp = LocalDateTime.of(2020, 1, 15, 4, 29);
		Member member = new Member("id", null, null, null, beforeTimestamp, null, null);

		assertTrue(retentionPolicy.matchesPolicy(member));
	}

	@Test
	void when_TimestampOfMemberAfterPointInTime_then_ItDoesNotMatchThePointInTimeRetentionPolicy() {
		LocalDateTime afterTimestamp = LocalDateTime.of(2020, 1, 15, 4, 31);
		Member member = new Member("id", null, null, null, afterTimestamp, null, null);

		assertFalse(retentionPolicy.matchesPolicy(member));
	}
}
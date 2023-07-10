package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.pointintime;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PointInTimeRetentionPolicyTest {

	private final RetentionPolicy retentionPolicy = new PointInTimeRetentionPolicy(
			LocalDateTime.of(2020, 1, 15, 4, 30));

	@Test
	void when_TimestampOfMemberIsNull_then_ItDoesNotMatchThePointInTimeRetentionPolicy() {
		MemberProperties memberProperties = new MemberProperties("id", null, null, null);

		assertFalse(retentionPolicy.matchesPolicyOfView(memberProperties, viewName));
	}

	@Test
	void when_TimestampOfMemberBeforePointInTime_then_ItMatchesThePointInTimeRetentionPolicy() {
		LocalDateTime beforeTimestamp = LocalDateTime.of(2020, 1, 15, 4, 29);
		MemberProperties memberProperties = new MemberProperties("id", null, null, beforeTimestamp);

		assertTrue(retentionPolicy.matchesPolicyOfView(memberProperties, viewName));
	}

	@Test
	void when_TimestampOfMemberAfterPointInTime_then_ItDoesNotMatchThePointInTimeRetentionPolicy() {
		LocalDateTime afterTimestamp = LocalDateTime.of(2020, 1, 15, 4, 31);
		MemberProperties memberProperties = new MemberProperties("id", null, null, afterTimestamp);

		assertFalse(retentionPolicy.matchesPolicyOfView(memberProperties, viewName));
	}
}
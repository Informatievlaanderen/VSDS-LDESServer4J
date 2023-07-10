package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class TimeBasedRetentionPolicyTest {

	RetentionPolicy retentionPolicy = new TimeBasedRetentionPolicy(Duration.of(1, ChronoUnit.SECONDS));

	@Test
	void when_TimestampOfMemberIsNull_ItDoesNotMatchTheTimebasedRetentionPolicy() {
		MemberProperties memberProperties = new MemberProperties("id", null, null, null);
		assertFalse(retentionPolicy.matchesPolicyOfView(memberProperties, viewName));
	}

	@Test
	void when_TimestampOfMemberIsLongEnoughAgo_ItMatchesTheTimebasedRetentionPolicy() {
		MemberProperties memberProperties = new MemberProperties("id", null, null, LocalDateTime.now());
		await().atMost(2, TimeUnit.SECONDS).until(() -> retentionPolicy.matchesPolicyOfView(memberProperties, viewName));
	}
}

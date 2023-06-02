package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TimeBasedRetentionPolicyTest {

	RetentionPolicy retentionPolicy = new TimeBasedRetentionPolicy(Duration.of(1, ChronoUnit.SECONDS));

	@Test
	void when_TimestampOfMemberIsNull_ItDoesNotMatchTheTimebasedRetentionPolicy() {
		Member ldesFragment = new Member("id", null, null, null, null, null, null);
		assertFalse(retentionPolicy.matchesPolicy(ldesFragment));
	}

	@Test
	void when_TimestampOfMemberIsLongEnoughAgo_ItMatchesTheTimebasedRetentionPolicy() {
		Member ldesFragment = new Member("id", null, null, null, LocalDateTime.now(), null, null);
		await().atMost(2, TimeUnit.SECONDS).until(() -> retentionPolicy.matchesPolicy(ldesFragment));
	}
}

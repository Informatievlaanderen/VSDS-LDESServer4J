package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TimeBasedRetentionPolicyTest {

	RetentionPolicy retentionPolicy = new TimeBasedRetentionPolicy("PT1S");

	@Test
	void when_FragmentIsLongEnoughImmutable_ItMatchesTheTimebasedRetentionPolicy() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("view", List.of()));

		assertFalse(retentionPolicy.matchesPolicy(ldesFragment));
		ldesFragment.makeImmutable();
		assertFalse(retentionPolicy.matchesPolicy(ldesFragment));
		await().atMost(2, TimeUnit.SECONDS).until(() -> retentionPolicy.matchesPolicy(ldesFragment));
	}
}
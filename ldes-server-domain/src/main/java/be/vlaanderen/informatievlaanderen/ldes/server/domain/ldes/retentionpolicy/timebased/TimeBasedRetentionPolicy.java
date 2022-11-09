package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import java.time.LocalDateTime;

public class TimeBasedRetentionPolicy implements RetentionPolicy {
	private final long durationInSeconds;

	public TimeBasedRetentionPolicy(long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	@Override
	public boolean matchesPolicy(LdesFragment ldesFragment) {
		LocalDateTime immutableTimestamp = ldesFragment.getFragmentInfo().getImmutableTimestamp();
		return immutableTimestamp != null
				&& LocalDateTime.now().isAfter(immutableTimestamp.plusSeconds(durationInSeconds));
	}
}

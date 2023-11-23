package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;

import java.time.Duration;

// TODO TVB: 23/11/23 test
public class TimeBasedRetentionPolicy implements RetentionPolicy {

	private final Duration duration;

	public TimeBasedRetentionPolicy(Duration duration) {
		this.duration = duration;
	}

	@Override
	public RetentionPolicyType getType() {
		return RetentionPolicyType.TIME_BASED;
	}

	public Duration getDuration() {
		return duration;
	}

}

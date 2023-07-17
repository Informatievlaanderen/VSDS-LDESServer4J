package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeBasedRetentionPolicy implements RetentionPolicy {
	private final Duration duration;

	public TimeBasedRetentionPolicy(Duration duration) {
		this.duration = duration;
	}

	@Override
	public boolean matchesPolicyOfView(MemberProperties memberProperties, String viewName) {
		LocalDateTime timestamp = memberProperties.getTimestamp();
		return timestamp != null
				&& LocalDateTime.now().isAfter(timestamp.plus(duration));
	}
}

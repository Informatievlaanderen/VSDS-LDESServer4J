package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.pointintime;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

import java.time.LocalDateTime;

public class PointInTimeRetentionPolicy implements RetentionPolicy {

	private final LocalDateTime pointInTime;

	public PointInTimeRetentionPolicy(LocalDateTime pointInTime) {
		this.pointInTime = pointInTime;
	}

	@Override
	public boolean matchesPolicy(MemberProperties memberProperties) {
		LocalDateTime timestamp = memberProperties.getTimestamp();
		return timestamp != null
				&& timestamp.isBefore(pointInTime);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.pointintime;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.time.LocalDateTime;

public class PointInTimeRetentionPolicy implements RetentionPolicy {

	private final LocalDateTime pointInTime;

	public PointInTimeRetentionPolicy(LocalDateTime pointInTime) {
		this.pointInTime = pointInTime;
	}

	@Override
	public boolean matchesPolicy(Member member) {
		LocalDateTime timestamp = member.getTimestamp();
		return timestamp != null
				&& timestamp.isBefore(pointInTime);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.DurationParser;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.time.LocalDateTime;

public class TimeBasedRetentionPolicy implements RetentionPolicy {
	private final String duration;

	public TimeBasedRetentionPolicy(String duration) {
		this.duration = duration;
	}

	@Override
	public boolean matchesPolicy(Member member) {
		LocalDateTime immutableTimestamp = member.getTimestamp();
		return immutableTimestamp != null
				&& LocalDateTime.now().isAfter(immutableTimestamp.plus(DurationParser.parseText(duration)));
	}
}

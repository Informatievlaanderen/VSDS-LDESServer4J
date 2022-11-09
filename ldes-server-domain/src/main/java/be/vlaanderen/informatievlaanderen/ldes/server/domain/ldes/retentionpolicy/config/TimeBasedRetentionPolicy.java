package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.config;

public class TimeBasedRetentionPolicy {
	private final Long durationInSeconds;

	public TimeBasedRetentionPolicy(Long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}
}

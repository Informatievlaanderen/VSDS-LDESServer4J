package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;

import java.time.Duration;

public record TimeBasedRetentionPolicy(Duration duration) implements RetentionPolicy {

	@Override
	public RetentionPolicyType getType() {
		return RetentionPolicyType.TIME_BASED;
	}

}

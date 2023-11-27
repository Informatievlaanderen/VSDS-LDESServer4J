package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;

import java.time.Duration;

public record TimeAndVersionBasedRetentionPolicy(Duration duration,
                                                 int numberOfMembersToKeep) implements RetentionPolicy {

    @Override
    public RetentionPolicyType getType() {
        return RetentionPolicyType.TIME_AND_VERSION_BASED;
    }

}

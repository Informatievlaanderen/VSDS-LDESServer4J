package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;

import java.time.Duration;

// TODO TVB: 23/11/23 test
public class TimeAndVersionBasedRetentionPolicy implements RetentionPolicy {

    private final Duration duration;
    private final int numberOfMembersToKeep;

    public TimeAndVersionBasedRetentionPolicy(Duration duration, int numberOfMembersToKeep) {
        this.duration = duration;
        this.numberOfMembersToKeep = numberOfMembersToKeep;
    }

    @Override
    public RetentionPolicyType getType() {
        return RetentionPolicyType.TIME_AND_VERSION_BASED;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getNumberOfMembersToKeep() {
        return numberOfMembersToKeep;
    }

}

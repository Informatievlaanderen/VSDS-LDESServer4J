package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

import java.time.Duration;

public record TimeAndVersionBasedRetentionPolicy(Duration duration,
                                                 int numberOfMembersToKeep) implements RetentionPolicy {

    // TODO TVB: 27/11/23 test me
    public static TimeAndVersionBasedRetentionPolicy from(RetentionPolicy policyA, RetentionPolicy policyB) {
        if (policyA instanceof TimeBasedRetentionPolicy timeBasedPolicy) {
            verifyIsTypeVersionBased(policyB);
            final int numberOfMembersToKeep = ((VersionBasedRetentionPolicy) policyB).numberOfMembersToKeep();
            final Duration duration = timeBasedPolicy.duration();
            return new TimeAndVersionBasedRetentionPolicy(duration, numberOfMembersToKeep);
        } else if (policyB instanceof TimeBasedRetentionPolicy timeBasedPolicy) {
            verifyIsTypeVersionBased(policyA);
            final int numberOfMembersToKeep = ((VersionBasedRetentionPolicy) policyA).numberOfMembersToKeep();
            final Duration duration = timeBasedPolicy.duration();
            return new TimeAndVersionBasedRetentionPolicy(duration, numberOfMembersToKeep);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void verifyIsTypeVersionBased(RetentionPolicy maybeVersionBasedPolicy) {
        if (!(maybeVersionBasedPolicy instanceof VersionBasedRetentionPolicy)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public RetentionPolicyType getType() {
        return RetentionPolicyType.TIME_AND_VERSION_BASED;
    }

}

package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicyType;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

import java.time.Duration;

import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.TIME_BASED_RETENTION_POLICY;
import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.VERSION_BASED_RETENTION_POLICY;

public record TimeAndVersionBasedRetentionPolicy(Duration duration,
                                                 int numberOfMembersToKeep) implements RetentionPolicy {

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
            throw timebasedAndVersionBasedRequiredException();
        }
    }

    private static void verifyIsTypeVersionBased(RetentionPolicy maybeVersionBasedPolicy) {
        if (!(maybeVersionBasedPolicy instanceof VersionBasedRetentionPolicy)) {
            throw timebasedAndVersionBasedRequiredException();
        }
    }

    private static IllegalArgumentException timebasedAndVersionBasedRequiredException() {
        return new IllegalArgumentException(
                "TimeAndVersionBasedRetentionPolicy requires exactly one %s and one %s"
                        .formatted(TIME_BASED_RETENTION_POLICY, VERSION_BASED_RETENTION_POLICY)
        );
    }

    @Override
    public RetentionPolicyType getType() {
        return RetentionPolicyType.TIME_AND_VERSION_BASED;
    }

}

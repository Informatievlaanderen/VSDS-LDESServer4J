package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.TIME_BASED_RETENTION_POLICY;
import static be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyConstants.VERSION_BASED_RETENTION_POLICY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeAndVersionBasedRetentionPolicyTest {

    @Test
    void should_ReturnInstance_when_TimeAndVersionPoliciesAreProvided() {
        Duration duration = Duration.ZERO;
        TimeBasedRetentionPolicy timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(duration);
        int membersToKeep = 1;
        VersionBasedRetentionPolicy versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(membersToKeep);

        var timeAndVersionResult =
                TimeAndVersionBasedRetentionPolicy.from(timeBasedRetentionPolicy, versionBasedRetentionPolicy);
        assertThat(timeAndVersionResult.duration()).isEqualTo(duration);
        assertThat(timeAndVersionResult.numberOfMembersToKeep()).isEqualTo(membersToKeep);

        var versionAndTimeResult =
                TimeAndVersionBasedRetentionPolicy.from(versionBasedRetentionPolicy, timeBasedRetentionPolicy);
        assertThat(versionAndTimeResult.duration()).isEqualTo(duration);
        assertThat(versionAndTimeResult.numberOfMembersToKeep()).isEqualTo(membersToKeep);
    }

    @Test
    void should_ThrowException_when_RetentionPolicyIsNotVersionOrTimeBased() {
        assertThatThrownBy(() -> TimeAndVersionBasedRetentionPolicy.from(
                new TimeBasedRetentionPolicy(Duration.ZERO),
                new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TimeAndVersionBasedRetentionPolicy requires exactly one %s and one %s",
                        TIME_BASED_RETENTION_POLICY, VERSION_BASED_RETENTION_POLICY);

        assertThatThrownBy(() -> TimeAndVersionBasedRetentionPolicy.from(
                new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1),
                new TimeBasedRetentionPolicy(Duration.ZERO)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TimeAndVersionBasedRetentionPolicy requires exactly one %s and one %s",
                        TIME_BASED_RETENTION_POLICY, VERSION_BASED_RETENTION_POLICY);

        assertThatThrownBy(() -> TimeAndVersionBasedRetentionPolicy.from(
                new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1),
                new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TimeAndVersionBasedRetentionPolicy requires exactly one %s and one %s",
                        TIME_BASED_RETENTION_POLICY, VERSION_BASED_RETENTION_POLICY);
    }

}
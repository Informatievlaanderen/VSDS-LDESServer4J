package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventSourceRetentionPolicyProviderTest {
	private static final String COLLECTION = "collection";
	private static final RetentionPolicy RETENTION_POLICY = new TimeBasedRetentionPolicy(Duration.ZERO);
	private static final EventSourceRetentionPolicyProvider eventSourceRetentionPolicyProvider = new EventSourceRetentionPolicyProvider(COLLECTION, RETENTION_POLICY);

	@Test
	void testEquality() {
		final EventSourceRetentionPolicyProvider allFieldsEqual = new EventSourceRetentionPolicyProvider(COLLECTION, RETENTION_POLICY);
		final EventSourceRetentionPolicyProvider otherRetentionPolicy = new EventSourceRetentionPolicyProvider(COLLECTION, new VersionBasedRetentionPolicy(3));

		assertThat(eventSourceRetentionPolicyProvider)
				.isEqualTo(allFieldsEqual)
				.isEqualTo(otherRetentionPolicy)
				.isEqualTo(eventSourceRetentionPolicyProvider)
				.hasSameHashCodeAs(allFieldsEqual)
				.hasSameHashCodeAs(otherRetentionPolicy)
				.hasSameHashCodeAs(eventSourceRetentionPolicyProvider);
	}

	@ParameterizedTest
	@MethodSource
	void testInequality(Object other) {
		assertThat(other).isNotEqualTo(eventSourceRetentionPolicyProvider);

		if(other != null) {
			assertThat(other).doesNotHaveSameHashCodeAs(eventSourceRetentionPolicyProvider);
		}
	}

	static Stream<Object> testInequality() {
		return Stream.of(
				new EventSourceRetentionPolicyProvider("other", RETENTION_POLICY),
				new EventSourceRetentionPolicyProvider("fantasy", new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 2)),
				new ViewRetentionPolicyProvider(new ViewName(COLLECTION, "fantasy"), RETENTION_POLICY),
				RETENTION_POLICY,
				COLLECTION,
				null
		);
	}
}
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

class ViewRetentionPolicyProviderTest {
	private static final String COLLECTION = "collection";
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
	private static final RetentionPolicy RETENTION_POLICY = new TimeBasedRetentionPolicy(Duration.ZERO);
	private static final ViewRetentionPolicyProvider viewRetentionPolicyProvider = new ViewRetentionPolicyProvider(VIEW_NAME, RETENTION_POLICY);

	@Test
	void testEquality() {
		final ViewRetentionPolicyProvider allFieldsEqual = new ViewRetentionPolicyProvider(VIEW_NAME, RETENTION_POLICY);
		final ViewRetentionPolicyProvider otherRetentionPolicy = new ViewRetentionPolicyProvider(VIEW_NAME, new VersionBasedRetentionPolicy(3));

		assertThat(viewRetentionPolicyProvider)
				.isEqualTo(allFieldsEqual)
				.isEqualTo(otherRetentionPolicy)
				.isEqualTo(viewRetentionPolicyProvider)
				.hasSameHashCodeAs(allFieldsEqual)
				.hasSameHashCodeAs(otherRetentionPolicy)
				.hasSameHashCodeAs(viewRetentionPolicyProvider);
	}

	@ParameterizedTest
	@MethodSource
	void testInequality(Object other) {
		assertThat(other).isNotEqualTo(viewRetentionPolicyProvider);

		if(other != null) {
			assertThat(other).doesNotHaveSameHashCodeAs(viewRetentionPolicyProvider);
		}
	}

	static Stream<Object> testInequality() {
		return Stream.of(
				new ViewRetentionPolicyProvider(new ViewName(COLLECTION, "fantasy"), RETENTION_POLICY),
				new ViewRetentionPolicyProvider(new ViewName("fantasy", VIEW), new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 2)),
				new ViewRetentionPolicyProvider(new ViewName("fantasy", "other"), new VersionBasedRetentionPolicy(6)),
				new EventSourceRetentionPolicyProvider("other", RETENTION_POLICY),
				RETENTION_POLICY,
				VIEW_NAME,
				VIEW_NAME.asString(),
				null
		);
	}}
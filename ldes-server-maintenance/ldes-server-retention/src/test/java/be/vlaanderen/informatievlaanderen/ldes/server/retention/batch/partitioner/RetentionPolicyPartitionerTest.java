package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.partitioner;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.EventSourceRetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.RetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.ViewRetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.RetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetentionPolicyPartitionerTest {
	private static final String COLLECTION = "collection";

	@Mock
	private RetentionPolicyCollection<RetentionPolicyProvider> retentionPolicies;
	@InjectMocks
	private RetentionPolicyPartitioner retentionPartitioner;

	@Nested
	class ViewRetentionPolicy {
		private static final ViewName VIEW_A = new ViewName(COLLECTION, "viewA");
		private static final ViewName VIEW_B = new ViewName(COLLECTION, "viewB");
		private static final ViewName VIEW_C = new ViewName(COLLECTION, "viewC");

		@Test
		void given_NonEmptyCollection_testPartitioning() {
			RetentionPolicy timeBasedRetentionPolicy = new TimeBasedRetentionPolicy(Duration.ZERO);
			RetentionPolicy versionBasedRetentionPolicy = new VersionBasedRetentionPolicy(1);
			RetentionPolicy timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);

			when(retentionPolicies.getRetentionPolicies()).thenReturn(Set.of(
					new ViewRetentionPolicyProvider(VIEW_A, timeBasedRetentionPolicy),
					new ViewRetentionPolicyProvider(VIEW_B, versionBasedRetentionPolicy),
					new ViewRetentionPolicyProvider(VIEW_C, timeAndVersionBasedRetentionPolicy)
			));

			final Map<String, ExecutionContext> result = retentionPartitioner.partition(0);

			assertThat(result)
					.hasSize(3)
					.containsEntry(
							"retention:%s".formatted(VIEW_A.asString()),
							new ExecutionContext(Map.of(
									"name", VIEW_A.asString(),
									"retentionPolicy", timeBasedRetentionPolicy
							))
					);
		}

		@Test
		void given_EmptyCollection_testPartitioning() {
			when(retentionPolicies.getRetentionPolicies()).thenReturn(Set.of());

			final Map<String, ExecutionContext> result = retentionPartitioner.partition(0);

			assertThat(result).isEmpty();
		}
	}

	@Nested
	class EventSourceRetentionPolicy {

		@Test
		void given_NonEmptyCollection_testPartitioning() {
			RetentionPolicy timeAndVersionBasedRetentionPolicy = new TimeAndVersionBasedRetentionPolicy(Duration.ZERO, 1);

			when(retentionPolicies.getRetentionPolicies()).thenReturn(Set.of(
					new EventSourceRetentionPolicyProvider(COLLECTION, timeAndVersionBasedRetentionPolicy)
			));

			final Map<String, ExecutionContext> result = retentionPartitioner.partition(0);

			assertThat(result)
					.hasSize(1)
					.containsEntry(
							"retention:%s".formatted(COLLECTION),
							new ExecutionContext(Map.of(
									"name", COLLECTION,
									"retentionPolicy", timeAndVersionBasedRetentionPolicy
							))
					);
		}

		@Test
		void given_EmptyCollection_testPartitioning() {
			when(retentionPolicies.getRetentionPolicies()).thenReturn(Set.of());

			final Map<String, ExecutionContext> result = retentionPartitioner.partition(0);

			assertThat(result).isEmpty();
		}
	}
}
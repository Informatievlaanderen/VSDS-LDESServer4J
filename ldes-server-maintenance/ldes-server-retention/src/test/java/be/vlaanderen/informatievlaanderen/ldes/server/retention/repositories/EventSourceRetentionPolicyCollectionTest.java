package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.EventSourceRetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.EventSourceRetentionPolicyCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventSourceRetentionPolicyCollectionTest {
	private static final String COLLECTION_NAME = "collectionName";
	private List<Model> retentionPolicies;
	@Mock
	private RetentionPolicyFactory retentionPolicyFactory;
	private EventSourceRetentionPolicyCollection deletionPolicyCollection;

	@BeforeEach
	void setUp() {
		deletionPolicyCollection = new EventSourceRetentionPolicyCollection(retentionPolicyFactory);
	}

	@Test
	void when_SavePolicies_Then_PoliciesAreSaved() {
		retentionPolicies = List.of(ModelFactory.createDefaultModel());
		when(retentionPolicyFactory.extractRetentionPolicy(retentionPolicies))
				.thenReturn(Optional.of(new TimeBasedRetentionPolicy(Duration.ZERO)));

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		assertThat(deletionPolicyCollection.getRetentionPolicies())
				.map(EventSourceRetentionPolicyProvider::collectionName)
				.contains(COLLECTION_NAME);
	}

	@Test
	void when_SavePoliciesAreEmpty_Then_PoliciesAreRemoved() {
		retentionPolicies = List.of(ModelFactory.createDefaultModel());

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		retentionPolicies = List.of();

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		assertThat(deletionPolicyCollection.getRetentionPolicies())
				.map(EventSourceRetentionPolicyProvider::collectionName)
				.doesNotContain(COLLECTION_NAME);
	}

	@Test
	void when_SavePoliciesAreEmpty_Then_PoliciesAreRemovedByCollection() {
		retentionPolicies = List.of(ModelFactory.createDefaultModel());

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		retentionPolicies = List.of();

		deletionPolicyCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION_NAME));

		assertThat(deletionPolicyCollection.getRetentionPolicies())
				.map(EventSourceRetentionPolicyProvider::collectionName)
				.doesNotContain(COLLECTION_NAME);
	}

	@Test
	void test_IsEmpty() {
		assertThat(deletionPolicyCollection.isEmpty()).isTrue();
	}
}
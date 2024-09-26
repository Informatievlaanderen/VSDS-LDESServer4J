package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.EventSourceLevelRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.EventSourceRetentionPolicyCollectionImpl;
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
class EventSourceRetentionPolicyCollectionImplTest {
	private static final String COLLECTION_NAME = "collectionName";
	private List<Model> retentionPolicies;
	@Mock
	private RetentionPolicyFactory retentionPolicyFactory;
	private EventSourceRetentionPolicyCollectionImpl deletionPolicyCollection;

	@BeforeEach
	void setUp() {
		deletionPolicyCollection = new EventSourceRetentionPolicyCollectionImpl(retentionPolicyFactory);
	}

	@Test
	void when_SavePolicies_Then_PoliciesAreSaved() {
		retentionPolicies = List.of(ModelFactory.createDefaultModel());
		when(retentionPolicyFactory.extractRetentionPolicy(retentionPolicies))
				.thenReturn(Optional.of(new TimeBasedRetentionPolicy(Duration.ZERO)));

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		assertThat(deletionPolicyCollection.getRetentionPolicies())
				.map(EventSourceLevelRetentionPolicy::collectionName)
				.contains(COLLECTION_NAME);
	}

	@Test
	void when_SavePoliciesAreEmpty_Then_PoliciesAreRemoved() {
		retentionPolicies = List.of(ModelFactory.createDefaultModel());

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		retentionPolicies = List.of();

		deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

		assertThat(deletionPolicyCollection.getRetentionPolicies())
				.map(EventSourceLevelRetentionPolicy::collectionName)
				.doesNotContain(COLLECTION_NAME);
	}
}
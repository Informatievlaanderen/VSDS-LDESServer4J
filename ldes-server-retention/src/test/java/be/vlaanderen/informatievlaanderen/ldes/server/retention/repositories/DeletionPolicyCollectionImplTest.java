package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeletionPolicyCollectionImplTest {
    private static final String COLLECTION_NAME = "collectionName";
    private List<Model> retentionPolicies;
    @Mock
    private RetentionPolicyFactory retentionPolicyFactory;
    private DeletionPolicyCollectionImpl deletionPolicyCollection;

    @BeforeEach
    void setUp() {
        deletionPolicyCollection = new DeletionPolicyCollectionImpl(retentionPolicyFactory);
    }

    @Test
    void when_SavePolicies_Then_PoliciesAreSaved() {
        retentionPolicies = List.of(ModelFactory.createDefaultModel());

        deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

        assertThat(deletionPolicyCollection.getEventSourceRetentionPolicyMap())
                .matches(map -> map.containsKey(COLLECTION_NAME));
    }

    @Test
    void when_SavePoliciesAreEmpty_Then_PoliciesAreRemoved() {
        retentionPolicies = List.of(ModelFactory.createDefaultModel());

        deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

        retentionPolicies = List.of();

        deletionPolicyCollection.handleDeletionPolicyChangedEvent(new DeletionPolicyChangedEvent(COLLECTION_NAME, retentionPolicies));

        assertThat(deletionPolicyCollection.getEventSourceRetentionPolicyMap())
                .matches(map -> !map.containsKey(COLLECTION_NAME));
    }
}
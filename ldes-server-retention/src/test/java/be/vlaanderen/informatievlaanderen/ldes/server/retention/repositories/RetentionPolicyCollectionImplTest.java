package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RetentionPolicyCollectionImplTest {
    private final RetentionPolicyFactory retentionPolicyFactory = mock(RetentionPolicyFactory.class);
    private final RetentionPolicyCollectionImpl retentionPolicyCollection = new RetentionPolicyCollectionImpl(
            retentionPolicyFactory);

    @Test
    void test_AddingAndDeletingViews() {
        ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
                List.of(), List.of(), 100);
        when(retentionPolicyFactory.extractRetentionPolicy(viewSpecification))
                .thenReturn(Optional.of(mock(RetentionPolicy.class)));

        assertThat(retentionPolicyCollection.getRetentionPolicyMap()).doesNotContainKey(viewSpecification.getName());
        retentionPolicyCollection.handleViewAddedEvent(new ViewAddedEvent(this, viewSpecification));

        assertThat(retentionPolicyCollection.getRetentionPolicyMap()).containsKey(viewSpecification.getName());
        retentionPolicyCollection.handleViewDeletedEvent(new ViewDeletedEvent(this, viewSpecification.getName()));
        assertThat(retentionPolicyCollection.getRetentionPolicyMap()).doesNotContainKey(viewSpecification.getName());
    }

    @Test
    void test_InitializingViews() {
        List<Model> retentionPolicies = new ArrayList<>();
        ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
                retentionPolicies, List.of(), 100);
        when(retentionPolicyFactory.extractRetentionPolicy(viewSpecification))
                .thenReturn(Optional.of(mock(RetentionPolicy.class)));
        assertThat(retentionPolicyCollection.getRetentionPolicyMap()).doesNotContainKey(viewSpecification.getName());

        retentionPolicyCollection.handleViewInitializationEvent(new ViewInitializationEvent(this, viewSpecification));
        assertThat(retentionPolicyCollection.getRetentionPolicyMap()).containsKey(viewSpecification.getName());
    }

    @Test
    void test_HandleEventStreamDeletedEvent() {
        final String collectionName = "collection";
        when(retentionPolicyFactory.extractRetentionPolicy(any(ViewSpecification.class))).thenReturn(Optional.of(mock(RetentionPolicy.class)));
        Stream.of(
                        new ViewSpecification(new ViewName(collectionName, "view1"), List.of(), List.of(), 100),
                        new ViewSpecification(new ViewName(collectionName, "view2"), List.of(), List.of(), 100)
                )
                .map(view -> new ViewInitializationEvent(this, view))
                .forEach(retentionPolicyCollection::handleViewInitializationEvent);

        retentionPolicyCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(this, collectionName));

        assertThat(retentionPolicyCollection.getRetentionPolicyMap()).isEmpty();
    }
}

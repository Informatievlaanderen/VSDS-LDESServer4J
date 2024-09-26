package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.ViewRetentionPolicyProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.retentionpolicies.ViewRetentionPolicyCollectionImpl;
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

class ViewRetentionPolicyCollectionTest {
	private final RetentionPolicyFactory retentionPolicyFactory = mock(RetentionPolicyFactory.class);
	private final ViewRetentionPolicyCollectionImpl retentionPolicyCollection = new ViewRetentionPolicyCollectionImpl(
			retentionPolicyFactory);

	@Test
	void test_AddingAndDeletingViews() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				List.of(), List.of(), 100);
		when(retentionPolicyFactory.extractRetentionPolicy(viewSpecification))
				.thenReturn(Optional.of(mock(RetentionPolicy.class)));

		assertThat(retentionPolicyCollection.getRetentionPolicies())
				.map(ViewRetentionPolicyProvider::viewName)
				.doesNotContain(viewSpecification.getName());
		retentionPolicyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertThat(retentionPolicyCollection.getRetentionPolicies())
				.map(ViewRetentionPolicyProvider::viewName)
				.contains(viewSpecification.getName());
		retentionPolicyCollection.handleViewDeletedEvent(new ViewDeletedEvent(viewSpecification.getName()));
		assertThat(retentionPolicyCollection.getRetentionPolicies())
				.map(ViewRetentionPolicyProvider::viewName)
				.doesNotContain(viewSpecification.getName());
	}

	@Test
	void test_InitializingViews() {
		List<Model> retentionPolicies = new ArrayList<>();
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				retentionPolicies, List.of(), 100);
		when(retentionPolicyFactory.extractRetentionPolicy(viewSpecification))
				.thenReturn(Optional.of(mock(RetentionPolicy.class)));
		assertThat(retentionPolicyCollection.getRetentionPolicies()).
				map(ViewRetentionPolicyProvider::viewName)
				.doesNotContain(viewSpecification.getName());

		retentionPolicyCollection.handleViewAddedEvent(new ViewInitializationEvent(viewSpecification));
		assertThat(retentionPolicyCollection.getRetentionPolicies()).
				map(ViewRetentionPolicyProvider::viewName)
				.contains(viewSpecification.getName());
	}

	@Test
	void test_HandleEventStreamDeletedEvent() {
		final String collectionName = "collection";
		when(retentionPolicyFactory.extractRetentionPolicy(any(ViewSpecification.class))).thenReturn(Optional.of(mock(RetentionPolicy.class)));
		Stream.of(
						new ViewSpecification(new ViewName(collectionName, "view1"), List.of(), List.of(), 100),
						new ViewSpecification(new ViewName(collectionName, "view2"), List.of(), List.of(), 100)
				)
				.map(ViewInitializationEvent::new)
				.forEach(retentionPolicyCollection::handleViewAddedEvent);

		retentionPolicyCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(collectionName));

		assertThat(retentionPolicyCollection.getRetentionPolicies()).isEmpty();
	}
}

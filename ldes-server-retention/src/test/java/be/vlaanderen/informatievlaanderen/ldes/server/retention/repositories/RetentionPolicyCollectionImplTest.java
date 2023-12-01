package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

		assertFalse(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
		retentionPolicyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
		retentionPolicyCollection.handleViewDeletedEvent(new ViewDeletedEvent(viewSpecification.getName()));
		assertFalse(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
	}

	@Test
	void test_InitializingViews() {
		List<Model> retentionPolicies = new ArrayList<>();
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				retentionPolicies, List.of(), 100);
		when(retentionPolicyFactory.extractRetentionPolicy(viewSpecification))
				.thenReturn(Optional.of(mock(RetentionPolicy.class)));
		assertFalse(retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));

		retentionPolicyCollection.handleViewInitializationEvent(new ViewInitializationEvent(viewSpecification));
		assertTrue(retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
	}

}

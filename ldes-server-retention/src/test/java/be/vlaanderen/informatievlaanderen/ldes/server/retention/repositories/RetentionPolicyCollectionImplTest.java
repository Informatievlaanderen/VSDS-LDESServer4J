package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RetentionPolicyCollectionImplTest {

	private final RetentionPolicyFactory retentionPolicyFactory = mock(RetentionPolicyFactory.class);
	private final RetentionPolicyCollectionImpl retentionPolicyCollection = new RetentionPolicyCollectionImpl(
			retentionPolicyFactory);

	@Test
	void test_AddingAndDeletingViews() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				List.of(), List.of(), 100);

		assertFalse(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName().asString()));
		retentionPolicyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName().asString()));
		retentionPolicyCollection.handleViewDeletedEvent(new ViewDeletedEvent(viewSpecification.getName()));
		assertFalse(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName().asString()));
	}

	@Test
	void test_InitializingViews() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				List.of(), List.of(), 100);
		assertFalse(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName().asString()));

		retentionPolicyCollection.handleViewInitializationEvent(new ViewInitializationEvent(viewSpecification));

		assertTrue(
				retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName().asString()));
	}

}
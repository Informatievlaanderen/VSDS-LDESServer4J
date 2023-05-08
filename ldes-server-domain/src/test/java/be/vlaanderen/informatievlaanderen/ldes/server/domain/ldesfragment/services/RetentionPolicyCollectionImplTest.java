package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RetentionPolicyCollectionImplTest {

	private final RetentionPolicyCreator retentionPolicyCreator = mock(RetentionPolicyCreator.class);
	private final RetentionPolicyCollectionImpl retentionPolicyCollection = new RetentionPolicyCollectionImpl(
			retentionPolicyCreator);

	@Test
	void test_AddingAndDeletingViews() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				List.of(), List.of());

		assertFalse(retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
		retentionPolicyCollection.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
		retentionPolicyCollection.handleViewDeletedEvent(new ViewDeletedEvent(viewSpecification.getName()));
		assertFalse(retentionPolicyCollection.getRetentionPolicyMap().containsKey(viewSpecification.getName()));
	}

}
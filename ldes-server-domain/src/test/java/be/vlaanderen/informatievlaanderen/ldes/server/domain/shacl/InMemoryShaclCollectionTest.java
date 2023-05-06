package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InMemoryShaclCollectionTest {

	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
	private final ShaclShapeRepository shaclShapeRepository = mock(ShaclShapeRepository.class);
	private final InMemoryShaclCollection memoryShaclCollection = new InMemoryShaclCollection(shaclShapeRepository,
			eventPublisher);
	private static final String COLLECTION_NAME = "collection";

	@Test
	void test_InsertionAndRetrieval() {
		Model model = ModelFactory.createDefaultModel();

		ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME, model);
		memoryShaclCollection.saveShape(shaclShape);

		verify(shaclShapeRepository).saveShaclShape(shaclShape);

		verify(eventPublisher).publishEvent(any(ShaclChangedEvent.class));
		Optional<ShaclShape> retrievedShape = memoryShaclCollection.retrieveShape(COLLECTION_NAME);
		assertTrue(retrievedShape.isPresent());
		assertEquals(shaclShape, retrievedShape.get());
	}

	@Test
	void test_deletion() {
		Model model = ModelFactory.createDefaultModel();
		ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME, model);

		memoryShaclCollection.saveShape(shaclShape);

		assertTrue(memoryShaclCollection.retrieveShape(COLLECTION_NAME).isPresent());

		memoryShaclCollection.deleteShape(COLLECTION_NAME);
		verify(shaclShapeRepository).deleteShaclShape(COLLECTION_NAME);
		verify(eventPublisher).publishEvent(any(ShaclDeletedEvent.class));

		assertTrue(memoryShaclCollection.retrieveShape(COLLECTION_NAME).isEmpty());
	}

	@Test
	void initShapeConfig() {
		Model model = ModelFactory.createDefaultModel();
		ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME, model);
		ShaclShape shaclShape2 = new ShaclShape("otherCollection", model);
		when(shaclShapeRepository.retrieveAllShaclShapes()).thenReturn(List.of(shaclShape, shaclShape2));

		memoryShaclCollection.initShapeConfig();

		verify(eventPublisher, times(2)).publishEvent((ShaclChangedEvent) any());
		assertTrue(memoryShaclCollection.retrieveShape(COLLECTION_NAME).isPresent());
		assertTrue(memoryShaclCollection.retrieveShape("otherCollection").isPresent());
	}
}

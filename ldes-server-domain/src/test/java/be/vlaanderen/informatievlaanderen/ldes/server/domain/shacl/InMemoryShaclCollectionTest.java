package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryShaclCollectionTest {

	private final ShaclShapeRepository shaclShapeRepository = mock(ShaclShapeRepository.class);
	private final InMemoryShaclCollection memoryShaclCollection = new InMemoryShaclCollection(shaclShapeRepository);
	private static final String COLLECTION_NAME = "collection";

	@Test
	void test_InsertionAndRetrieval() {
		Model model = ModelFactory.createDefaultModel();

		ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME, model);
		memoryShaclCollection.saveShape(shaclShape);

		verify(shaclShapeRepository).saveShaclShape(shaclShape);

		Optional<ShaclShape> retrievedShape = memoryShaclCollection.retrieveShape(COLLECTION_NAME);
		assertTrue(retrievedShape.isPresent());
		assertEquals(shaclShape, retrievedShape.get());
	}

	@Test
	void test_handleEvent() {
		Model model = ModelFactory.createDefaultModel();

		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent(COLLECTION_NAME, model);
		memoryShaclCollection.handleShaclChangedEvent(shaclChangedEvent);

		verify(shaclShapeRepository).saveShaclShape(shaclChangedEvent.getShacl());
	}
}

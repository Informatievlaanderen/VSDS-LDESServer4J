package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
	void test_deletion() {
		Model model = ModelFactory.createDefaultModel();
		ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME, model);

		memoryShaclCollection.saveShape(shaclShape);

		assertTrue(memoryShaclCollection.retrieveShape(COLLECTION_NAME).isPresent());

		memoryShaclCollection.deleteShape(COLLECTION_NAME);
		verify(shaclShapeRepository).deleteShaclShape(COLLECTION_NAME);

		assertTrue(memoryShaclCollection.retrieveShape(COLLECTION_NAME).isEmpty());
	}
}

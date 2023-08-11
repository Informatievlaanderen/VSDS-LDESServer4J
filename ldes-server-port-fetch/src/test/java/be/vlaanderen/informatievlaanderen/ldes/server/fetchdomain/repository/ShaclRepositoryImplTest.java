package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.Shacl;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ShaclRepositoryImplTest {
	private static final String COLLECTION = "collection";
	private final ShaclRepository shaclRepository = new ShaclRepositoryImpl();

	@Test
	void when_ShaclIsSaved_then_ItCanBeRetrievedAndDeleted() {
		Shacl shacl = new Shacl(COLLECTION, ModelFactory.createDefaultModel());

		shaclRepository.saveShacl(shacl);
		Shacl actualShacl = shaclRepository.getShaclByCollection(COLLECTION);
		assertEquals(actualShacl, shacl);
		shaclRepository.deleteShaclByCollection(COLLECTION);
		assertNull(shaclRepository.getShaclByCollection(COLLECTION));
	}

}
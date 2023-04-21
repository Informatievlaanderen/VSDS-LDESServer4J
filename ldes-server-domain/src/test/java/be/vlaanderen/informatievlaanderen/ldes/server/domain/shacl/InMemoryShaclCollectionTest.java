package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryShaclCollectionTest {

	private final LdesConfigModelService ldesConfigModelService = mock(LdesConfigModelService.class);
	private final InMemoryShaclCollection memoryShaclCollection = new InMemoryShaclCollection(ldesConfigModelService);
	private static final String COLLECTION_NAME = "collection";

	@Test
	void test_InsertionAndRetrieval() {
		Model model = ModelFactory.createDefaultModel();

		memoryShaclCollection.handleShaclChangedEvent(new ShaclChangedEvent(COLLECTION_NAME, model));

		verify(ldesConfigModelService, times(1)).updateShape(eq(COLLECTION_NAME), any(LdesConfigModel.class));

		LdesConfigModel ldesConfigModel = memoryShaclCollection.retrieveShape(COLLECTION_NAME);
		assertEquals(COLLECTION_NAME, ldesConfigModel.getId());
		assertTrue(model.isIsomorphicWith(ldesConfigModel.getModel()));
	}

}
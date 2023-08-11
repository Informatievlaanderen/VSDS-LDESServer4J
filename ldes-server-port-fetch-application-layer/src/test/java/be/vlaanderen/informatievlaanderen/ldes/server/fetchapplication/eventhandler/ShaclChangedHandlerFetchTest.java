package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.Shacl;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.ShaclRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShaclChangedHandlerFetchTest {

	@Mock
	private ShaclRepository shaclRepository;
	@InjectMocks
	private ShaclChangedHandlerFetch shaclChangedHandlerFetch;

	@Test
	void when_HandleShaclChangedEvent_ShaclIsSavedInShaclRepository() {
		ShaclChangedEvent shaclChangedEvent = new ShaclChangedEvent("collection", ModelFactory.createDefaultModel());

		shaclChangedHandlerFetch.handleShaclChangedEvent(shaclChangedEvent);

		verify(shaclRepository).saveShacl(any(Shacl.class));
	}
}
package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.ShaclRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShaclDeletedHandlerFetchTest {

	public static final String COLLECTION_NAME = "collection";
	@Mock
	private ShaclRepository shaclRepository;
	@InjectMocks
	private ShaclDeletedHandlerFetch shaclDeletedHandlerFetch;

	@Test
	void when_HandleShaclDeletedEvent_ShaclRepositoryIsCalledWithDeleteCommand() {
		ShaclDeletedEvent shaclDeletedEvent = new ShaclDeletedEvent(COLLECTION_NAME);

		shaclDeletedHandlerFetch.handleShaclDeletedEvent(shaclDeletedEvent);

		verify(shaclRepository).deleteShaclByCollection(COLLECTION_NAME);
	}
}
package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ViewDeletedHandlerFetchTest {
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW_NAME = "viewName";
	@Mock
	private AllocationRepository allocationRepository;
	@InjectMocks
	private ViewDeletedHandlerFetch viewDeletedHandlerFetch;

	@Test
	void when_HandleViewDeletedEvent_AllocationRepositoryIsCalledWithDeleteCommand() {
		ViewDeletedEvent viewDeletedEvent = new ViewDeletedEvent(new ViewName(COLLECTION_NAME, VIEW_NAME));

		viewDeletedHandlerFetch.handleViewDeletedEvent(viewDeletedEvent);

		verify(allocationRepository).deleteByCollectionNameAndViewName(COLLECTION_NAME, VIEW_NAME);
	}

}
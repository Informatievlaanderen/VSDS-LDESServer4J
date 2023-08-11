package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberUnallocatedHandlerFetchTest {

	public static final String MEMBER_ID = "id";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW_NAME = "viewName";
	@Mock
	private AllocationRepository allocationRepository;

	@InjectMocks
	private MemberUnallocatedHandlerFetch memberUnallocatedHandlerFetch;

	@Test
	void when_HandleMemberUnallocatedEvent_AllocationRepositoryIsCalledWithDeleteCommand() {
		MemberUnallocatedEvent memberUnallocatedEvent = new MemberUnallocatedEvent(MEMBER_ID,
				new ViewName(COLLECTION_NAME, VIEW_NAME));

		memberUnallocatedHandlerFetch.handleMemberUnallocatedEvent(memberUnallocatedEvent);

		verify(allocationRepository).deleteByMemberIdAndCollectionNameAndViewName(MEMBER_ID, COLLECTION_NAME,
				VIEW_NAME);
	}

}
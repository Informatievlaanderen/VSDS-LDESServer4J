package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AllocatedMemberHandlerTest {
	private final static String ID = "id";
	private final static ViewName VIEW = new ViewName("collection", "view");
	private final static MemberAllocatedEvent event = new MemberAllocatedEvent(ID,
			fragment.getViewName().getCollectionName(), VIEW, "");
	private AllocatedMemberHandler handler;
	private MemberPropertiesRepository repo;

	@BeforeEach
	void setUp() {
		repo = mock(MemberPropertiesRepository.class);
		handler = new AllocatedMemberHandler(repo);
	}

	@Test
	void allocateMember() {
		handler.handleMemberAllocatedEvent(event);

		verify(repo).addViewReference(ID, VIEW.asString());
	}

}
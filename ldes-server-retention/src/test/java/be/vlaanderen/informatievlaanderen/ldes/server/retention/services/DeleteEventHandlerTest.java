package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution.MemberRemover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeleteEventHandlerTest {
	private static final String COLLECTION = "collection";
	private static final ViewName VIEW = new ViewName(COLLECTION, "view1");
	private MemberProperties member1 = new MemberProperties("1", COLLECTION, "version", LocalDateTime.now(), true);
	private MemberProperties member2 = new MemberProperties("2", COLLECTION, "version", LocalDateTime.now(), true);
	private ViewDeletedEvent event = new ViewDeletedEvent(VIEW);
	private MemberPropertiesRepository memberPropertiesRepository;
	private MemberRemover memberRemover;
	private DeleteEventHandler eventHandler;
	@Captor
	ArgumentCaptor<MemberProperties> captor;
	@Captor
	ArgumentCaptor<String> viewCaptor;

	@BeforeEach
	void setUp() {
		captor = ArgumentCaptor.forClass(MemberProperties.class);
		viewCaptor = ArgumentCaptor.forClass(String.class);
		member1.addViewReference(VIEW.asString());
		member2.addViewReference(VIEW.asString());
		member2.addViewReference("view2");
		memberPropertiesRepository = mock(MemberPropertiesRepository.class);
		memberRemover = mock(MemberRemover.class);
		eventHandler = new DeleteEventHandler(memberPropertiesRepository, memberRemover);
	}

	@Test
	void when_ViewDeleted_MemberRemovedFromView() {
		when(memberPropertiesRepository.getMemberPropertiesWithViewReference(VIEW))
				.thenReturn(Stream.of(member1, member2));

		eventHandler.handleViewDeletedEvent(event);

		verify(memberRemover, times(2)).removeMemberFromView(captor.capture(), viewCaptor.capture());
		assertEquals("1", captor.getAllValues().get(0).getId());
		assertEquals("2", captor.getAllValues().get(1).getId());
		assertEquals(VIEW.asString(), viewCaptor.getAllValues().get(0));
		assertEquals(VIEW.asString(), viewCaptor.getAllValues().get(1));

	}

	@Test
	void when_EventstreamDeleted_Then_CallMethod() {
		eventHandler.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION));

		verify(memberPropertiesRepository, times(1)).removeMemberPropertiesOfCollection(COLLECTION);
	}
}

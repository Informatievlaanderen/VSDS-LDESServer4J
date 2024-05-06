package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersRemovedFromEventSourceEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class MemberRemoverImplTest {

	public static final String VIEW_NAME = "collection/view";
	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	private MemberRemover memberRemover;

	@BeforeEach
	void setUp() {
		memberRemover = new MemberRemoverImpl(memberPropertiesRepository, applicationEventPublisher);
	}

	@Test
	void when_MemberPropertiesHas1ViewReference_then_ViewReferenceIsDeleted() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null, true);
		memberProperties.addViewReference(VIEW_NAME);
		assertTrue(memberProperties.containsViewReference(VIEW_NAME));

		memberRemover.removeMemberFromView(memberProperties, VIEW_NAME);

		assertFalse(memberProperties.containsViewReference(VIEW_NAME));
		InOrder inOrder = inOrder(memberPropertiesRepository, applicationEventPublisher);
		inOrder.verify(memberPropertiesRepository).removeViewReference(memberProperties.getId(), VIEW_NAME);
		inOrder.verify(applicationEventPublisher)
				.publishEvent(new MemberUnallocatedEvent(memberProperties.getId(), ViewName.fromString(VIEW_NAME)));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberPropertiesToBeREmovedFromEventSource_Then_MemberIsRemovedFromEventSource() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null, false);

		memberRemover.removeMembersFromEventSource(List.of(memberProperties));

		InOrder inOrder = inOrder(memberPropertiesRepository, applicationEventPublisher);
		inOrder.verify(memberPropertiesRepository).removeFromEventSource(List.of(memberProperties.getId()));
		inOrder.verify(applicationEventPublisher)
				.publishEvent(new MembersRemovedFromEventSourceEvent(List.of(memberProperties.getId())));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberPropertiesNotInEventSource_Then_MemberIsRemoved() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null, false);

		memberRemover.deleteMembers(List.of(memberProperties));

		InOrder inOrder = inOrder(memberPropertiesRepository, applicationEventPublisher);
		inOrder.verify(memberPropertiesRepository).deleteAllByIds(List.of(memberProperties.getId()));
		inOrder.verify(applicationEventPublisher)
				.publishEvent(new MembersDeletedEvent(List.of(memberProperties.getId())));
		inOrder.verifyNoMoreInteractions();
	}
}
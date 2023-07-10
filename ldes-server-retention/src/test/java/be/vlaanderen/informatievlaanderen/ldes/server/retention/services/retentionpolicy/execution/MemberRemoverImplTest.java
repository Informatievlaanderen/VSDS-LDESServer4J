package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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
	void when_MemberPropertiesHasMultipleViewReferences_then_ViewReferenceIsDeleted() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null);
		memberProperties.addViewReference(VIEW_NAME);
		memberProperties.addViewReference("otherView");
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
	void when_MemberPropertiesHasOneViewReference_then_ViewReferenceIsDeletedAndMemberIsDeleted() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null);
		memberProperties.addViewReference(VIEW_NAME);
		assertTrue(memberProperties.containsViewReference(VIEW_NAME));

		memberRemover.removeMemberFromView(memberProperties, VIEW_NAME);

		assertFalse(memberProperties.containsViewReference(VIEW_NAME));
		InOrder inOrder = inOrder(memberPropertiesRepository, applicationEventPublisher);
		inOrder.verify(memberPropertiesRepository).removeViewReference(memberProperties.getId(), VIEW_NAME);
		inOrder.verify(applicationEventPublisher)
				.publishEvent(new MemberUnallocatedEvent(memberProperties.getId(), ViewName.fromString(VIEW_NAME)));
		inOrder.verify(memberPropertiesRepository).deleteById(memberProperties.getId());
		inOrder.verify(applicationEventPublisher)
				.publishEvent(new MemberDeletedEvent(memberProperties.getId()));
		inOrder.verifyNoMoreInteractions();
	}

}
package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class MemberRemoverImplTest {

	public static final String VIEW_NAME = "view";
	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;
	private MemberRemover memberRemover;

	@BeforeEach
	void setUp() {
		memberRemover = new MemberRemoverImpl(memberPropertiesRepository);
	}

	@Test
	void when_MemberPropertiesHasMultipleViewReferences_then_ViewReferenceIsDeleted() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null);
		memberProperties.addViewReference(VIEW_NAME);
		memberProperties.addViewReference("otherView");
		assertTrue(memberProperties.containsViewReference(VIEW_NAME));

		memberRemover.removeMemberFromView(memberProperties, VIEW_NAME);

		assertFalse(memberProperties.containsViewReference(VIEW_NAME));
		InOrder inOrder = inOrder(memberPropertiesRepository);
		inOrder.verify(memberPropertiesRepository).removeViewReference(memberProperties.getId(), VIEW_NAME);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberPropertiesHasOneViewReference_then_ViewReferenceIsDeletedAndMemberIsDeleted() {
		MemberProperties memberProperties = new MemberProperties("1", null, null, null);
		memberProperties.addViewReference(VIEW_NAME);
		assertTrue(memberProperties.containsViewReference(VIEW_NAME));

		memberRemover.removeMemberFromView(memberProperties, VIEW_NAME);

		assertFalse(memberProperties.containsViewReference(VIEW_NAME));
		InOrder inOrder = inOrder(memberPropertiesRepository);
		inOrder.verify(memberPropertiesRepository).removeViewReference(memberProperties.getId(), VIEW_NAME);
		inOrder.verify(memberPropertiesRepository).deleteById(memberProperties.getId());
		inOrder.verifyNoMoreInteractions();
	}

}
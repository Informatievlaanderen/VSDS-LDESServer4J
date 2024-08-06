package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class MemberRemoverImplTest {

	public static final String VIEW_NAME = "collection/view";
	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;
	private MemberRemover memberRemover;

	@BeforeEach
	void setUp() {
		memberRemover = new MemberRemoverImpl(memberPropertiesRepository);
	}

	@Test
	void when_MemberPropertiesToBeRemovedFromEventSource_Then_MemberIsRemovedFromEventSource() {
		MemberProperties memberProperties = new MemberProperties(1L, "collection",
				"http://example.com", LocalDateTime.now(), false, true);
		MemberProperties memberProperties2 = new MemberProperties(2L, "collection",
				"http://example.com", LocalDateTime.now(), true, true);

		memberRemover.removeMembersFromEventSource(List.of(memberProperties, memberProperties2));

		InOrder inOrder = inOrder(memberPropertiesRepository);
		inOrder.verify(memberPropertiesRepository).removeFromEventSource(List.of(memberProperties2.id()));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberPropertiesNotInEventSource_Then_MemberIsRemoved() {
		MemberProperties memberProperties = new MemberProperties(1L, "collection",
				"http://example.com", LocalDateTime.now(), false, false);

		memberRemover.deleteMembers(List.of(memberProperties));

		InOrder inOrder = inOrder(memberPropertiesRepository);
		inOrder.verify(memberPropertiesRepository).deleteAllByIds(List.of(memberProperties.id()));
		inOrder.verifyNoMoreInteractions();
	}
}
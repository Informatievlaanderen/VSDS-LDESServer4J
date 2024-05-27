package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationServiceTest {

	@Mock
	private FragmentationStrategyCollection fragmentationStrategyCollection;

	@Mock
	private FragmentRepository fragmentRepository;

	@InjectMocks
	private FragmentationService fragmentationService;

	@Test
	void when_MemberIngestedEvent_then_AllFragmentationExecutorsFromThisCollection_should_BeTriggered() {
		final MembersIngestedEvent.MemberProperties memberProperties = new MembersIngestedEvent.MemberProperties("collectionName", "versionOf", LocalDateTime.now());
		final MembersIngestedEvent membersIngestedEvent = new MembersIngestedEvent("id", List.of(memberProperties));
		final FragmentationStrategyExecutor executorA = mock(FragmentationStrategyExecutor.class);
		final FragmentationStrategyExecutor executorB = mock(FragmentationStrategyExecutor.class);
		when(fragmentationStrategyCollection.getFragmentationStrategyExecutors(membersIngestedEvent.collectionName()))
				.thenReturn(List.of(executorA, executorB));

		fragmentationService.executeFragmentation(membersIngestedEvent);

		verify(executorA).execute();
		verify(executorB).execute();
	}

	@Test
	void when_EventStreamClosedEvent_then_FragmentsAreMadeImmutable() {
		EventStreamClosedEvent event = new EventStreamClosedEvent("collectionName");

		fragmentationService.markFragmentsImmutableInCollection(event);

		verify(fragmentRepository).markFragmentsImmutableInCollection("collectionName");
	}
}

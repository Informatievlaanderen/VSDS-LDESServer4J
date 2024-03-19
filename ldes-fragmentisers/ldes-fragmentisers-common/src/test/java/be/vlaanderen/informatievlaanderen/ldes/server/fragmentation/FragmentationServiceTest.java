package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
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

	@InjectMocks
	private FragmentationService fragmentationService;

	@Test
	void when_MemberIngestedEvent_then_AllFragmentationExecutorsFromThisCollection_should_BeTriggered() {
		final MemberIngestedEvent memberIngestedEvent = new MemberIngestedEvent("id",
				"collectionName", 1L, "versionOf", LocalDateTime.now());
		final FragmentationStrategyExecutor executorA = mock(FragmentationStrategyExecutor.class);
		final FragmentationStrategyExecutor executorB = mock(FragmentationStrategyExecutor.class);
		when(fragmentationStrategyCollection.getFragmentationStrategyExecutors(memberIngestedEvent.collectionName()))
				.thenReturn(List.of(executorA, executorB));

		fragmentationService.executeFragmentation(memberIngestedEvent);

		verify(executorA).execute();
		verify(executorB).execute();
	}

}

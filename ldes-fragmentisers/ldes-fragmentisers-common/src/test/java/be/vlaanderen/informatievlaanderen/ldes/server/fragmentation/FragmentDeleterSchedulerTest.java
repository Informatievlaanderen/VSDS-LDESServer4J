package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.FragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentDeleterSchedulerTest {

	@Mock
	ApplicationEventPublisher applicationEventPublisher;
	@Mock
	FragmentRepository fragmentRepository;
	@InjectMocks
	FragmentDeleterScheduler fragmentDeleterScheduler;

	@Test
	void when_FragmentHasDeleteTimeEarlierThanCurrentTime_then_ItIsDeletedAndEventIsSent() {
		Fragment expiredFragment = createFragment("mobility-hindrances/expired", LocalDateTime.now().minusDays(1));
		Fragment notExpiredFragment = createFragment("mobility-hindrances/not-expired",
				LocalDateTime.now().plusDays(1));
		when(fragmentRepository.getDeletionCandidates()).thenReturn(Stream.of(expiredFragment, notExpiredFragment));

		fragmentDeleterScheduler.deleteFragments();

		verify(fragmentRepository).getDeletionCandidates();
		verify(fragmentRepository).deleteFragmentAndRemoveRelationsPointingToFragment(expiredFragment);
		verify(applicationEventPublisher).publishEvent(new FragmentDeletedEvent(expiredFragment.getFragmentId()));
		verifyNoMoreInteractions(fragmentRepository, applicationEventPublisher);
	}

	private static Fragment createFragment(String viewName, LocalDateTime deleteTime) {
		return new Fragment(new LdesFragmentIdentifier(viewName, List.of()), false, 0, List.of(), deleteTime);
	}

}
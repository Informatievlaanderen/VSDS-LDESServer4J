package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.FragmentDeletionScheduler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkFragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentDeletionSchedulerTest {
	@Mock
	private RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker;
	@Mock
	private PageRepository pageRepository;
	private FragmentDeletionScheduler fragmentDeletionScheduler;

	@BeforeEach
	void setUp() {
		fragmentDeletionScheduler = new FragmentDeletionScheduler(pageRepository, retentionPolicyEmptinessChecker);
	}

	@Test
	void when_FragmentHasDeleteTimeEarlierThanCurrentTime_then_ItIsDeletedAndEventIsSent() {
		when(retentionPolicyEmptinessChecker.isEmpty()).thenReturn(false);

		fragmentDeletionScheduler.deleteFragments();

		verify(pageRepository).deleteOutdatedFragments(any());
	}

	@Test
	void given_RetentionPolicyCollectionIsEmpty_when_CompactFragments_then_DoNotRun() {
		when(retentionPolicyEmptinessChecker.isEmpty()).thenReturn(true);

		fragmentDeletionScheduler.deleteFragments();

		verifyNoInteractions(pageRepository);
	}
}
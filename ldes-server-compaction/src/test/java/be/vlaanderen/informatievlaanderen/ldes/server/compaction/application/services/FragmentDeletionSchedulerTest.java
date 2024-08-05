package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
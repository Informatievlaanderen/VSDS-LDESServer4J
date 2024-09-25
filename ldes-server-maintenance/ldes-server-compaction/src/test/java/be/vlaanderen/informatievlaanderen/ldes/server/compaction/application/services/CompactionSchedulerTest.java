package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services.RetentionPolicyEmptinessChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompactionSchedulerTest {
	@Mock
	private ViewCollection viewCollection;
	@Mock
	private PaginationCompactionService paginationCompactionService;
	@Mock
	private CompactionCandidateService compactionCandidateService;
	@Mock
	private RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker;

	@InjectMocks
	private CompactionScheduler compactionScheduler;


	@Test
	void given_RetentionPoliciesCollectionIsEmpty_when_CompactFragments_then_DoNotRun() {
		when(retentionPolicyEmptinessChecker.isEmpty()).thenReturn(true);

		compactionScheduler.compactFragments();

		verify(retentionPolicyEmptinessChecker).isEmpty();
		verifyNoMoreInteractions(viewCollection, paginationCompactionService, compactionCandidateService, retentionPolicyEmptinessChecker);
	}

	@Test
	void given_RetentionPoliciesCollectionIsNotEmpty_when_CompactFragments_then_DoRun() {
		when(retentionPolicyEmptinessChecker.isEmpty()).thenReturn(false);

		compactionScheduler.compactFragments();

		verify(retentionPolicyEmptinessChecker).isEmpty();
		verify(viewCollection).getAllViewCapacities();
		verifyNoMoreInteractions(viewCollection, paginationCompactionService, compactionCandidateService, retentionPolicyEmptinessChecker);
	}


}
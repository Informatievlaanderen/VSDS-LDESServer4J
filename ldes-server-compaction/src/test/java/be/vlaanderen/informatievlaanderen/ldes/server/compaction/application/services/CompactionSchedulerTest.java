package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
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
	private RetentionPolicyCollection retentionPolicyCollection;

	@InjectMocks
	private CompactionScheduler compactionScheduler;


	@Test
	void given_RetentionPoliciesCollectionIsEmpty_when_CompactFragments_then_DoNotRun() {
		when(retentionPolicyCollection.isEmpty()).thenReturn(true);

		compactionScheduler.compactFragments();

		verify(retentionPolicyCollection).isEmpty();
		verifyNoMoreInteractions(viewCollection, paginationCompactionService, compactionCandidateService, retentionPolicyCollection);
	}

	@Test
	void given_RetentionPoliciesCollectionIsNotEmpty_when_CompactFragments_then_DoRun() {
		when(retentionPolicyCollection.isEmpty()).thenReturn(false);

		compactionScheduler.compactFragments();

		verify(retentionPolicyCollection).isEmpty();
		verify(viewCollection).getAllViewCapacities();
		verifyNoMoreInteractions(viewCollection, paginationCompactionService, compactionCandidateService, retentionPolicyCollection);
	}


}
package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompactionTaskTest {
	@Mock
	private PageRepository pageRepository;
	@Mock
	private CompactionCandidateSorter compactionCandidateSorter;
	@Mock
	private CompactionDbWriter compactionDbWriter;
	@Mock
	private StepExecution stepExecution;
	@Mock
	private ExecutionContext executionContext;
	@InjectMocks
	private CompactionTask compactionTask;

	@BeforeEach
	void setUp() {
		when(stepExecution.getExecutionContext()).thenReturn(executionContext);
	}

	@Test
	void given_RetentionPoliciesCollectionIsNotEmpty_when_CompactFragments_then_DoRun() {
		final ViewName viewName = new ViewName("collection", "view");
		final int capacityPerPage = 125;
		final List<CompactionCandidate> candidates = IntStream.range(0, 5)
				.mapToObj(i -> mock(CompactionCandidate.class))
				.toList();
		final Collection<Set<CompactionCandidate>> taskList = IntStream.range(0, 5)
				.boxed()
				.collect(Collectors.toMap(Function.identity(), i -> Set.copyOf(new HashSet<CompactionCandidate>())))
				.values();

		when(executionContext.getString("viewName")).thenReturn(viewName.asString());
		when(executionContext.getInt("capacityPerPage")).thenReturn(capacityPerPage);
		when(pageRepository.getPossibleCompactionCandidates(viewName, capacityPerPage)).thenReturn(candidates);
		when(compactionCandidateSorter.getCompactionCandidateList(candidates, capacityPerPage)).thenReturn(taskList);

		compactionTask.execute(mock(), new ChunkContext(new StepContext(stepExecution)));

		verify(pageRepository).getPossibleCompactionCandidates(viewName, capacityPerPage);
		verify(compactionCandidateSorter).getCompactionCandidateList(candidates, capacityPerPage);
		verify(compactionDbWriter, times(taskList.size())).writeToDb(anySet());
	}
}
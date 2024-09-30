package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompactionTask implements Tasklet {
	private final PageRepository pageRepository;
	private final CompactionCandidateSorter compactionCandidateSorter;
	private final CompactionDbWriter compactionDbWriter;

	public CompactionTask(PageRepository pageRepository, CompactionCandidateSorter compactionCandidateSorter, CompactionDbWriter compactionDbWriter) {
		this.pageRepository = pageRepository;
		this.compactionCandidateSorter = compactionCandidateSorter;
		this.compactionDbWriter = compactionDbWriter;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		final ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		final ViewName viewName = ViewName.fromString(executionContext.getString("viewName"));
		final int capacityPerPage = executionContext.getInt("capacityPerPage");

		final List<CompactionCandidate> compactionCandidates = pageRepository.getPossibleCompactionCandidates(viewName, capacityPerPage);

		compactionCandidateSorter
				.getCompactionCandidateList(compactionCandidates, capacityPerPage)
				.forEach(compactionDbWriter::writeToDb);

		return RepeatStatus.FINISHED;
	}
}

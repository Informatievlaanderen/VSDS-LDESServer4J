package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PageDeletionTask implements Tasklet {
	private final PageRepository pageRepository;

	public PageDeletionTask(PageRepository pageRepository) {
		this.pageRepository = pageRepository;
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		pageRepository.deleteOutdatedFragments(LocalDateTime.now());
		return RepeatStatus.FINISHED;
	}
}

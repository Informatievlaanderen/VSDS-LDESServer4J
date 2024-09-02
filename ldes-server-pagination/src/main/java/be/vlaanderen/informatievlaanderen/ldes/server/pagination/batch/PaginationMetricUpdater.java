package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.COLLECTION_NAME;

@Component
public class PaginationMetricUpdater implements StepExecutionListener {
	private final ServerMetrics serverMetrics;

	public PaginationMetricUpdater(ServerMetrics serverMetrics) {
		this.serverMetrics = serverMetrics;
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		serverMetrics.updatePaginationCounts(stepExecution.getJobParameters().getString(COLLECTION_NAME));
		return StepExecutionListener.super.afterStep(stepExecution);
	}
}

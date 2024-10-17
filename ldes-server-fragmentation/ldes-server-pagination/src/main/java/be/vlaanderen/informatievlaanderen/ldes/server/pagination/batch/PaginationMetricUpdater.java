package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.FragmentationMetricsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.COLLECTION_NAME;

@Component
public class PaginationMetricUpdater implements StepExecutionListener {
	private final FragmentationMetricsService fragmentationMetricsService;

	public PaginationMetricUpdater(FragmentationMetricsService fragmentationMetricsService) {
		this.fragmentationMetricsService = fragmentationMetricsService;
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		fragmentationMetricsService.updatePaginationCounts(stepExecution.getJobParameters().getString(COLLECTION_NAME));
		return StepExecutionListener.super.afterStep(stepExecution);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.FragmentationMetricsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationJobScheduler.COLLECTION_NAME;

@Component
public class BucketMetricUpdater implements StepExecutionListener {
	private final FragmentationMetricsService fragmentationMetricsService;

	public BucketMetricUpdater(FragmentationMetricsService fragmentationMetricsService) {
		this.fragmentationMetricsService = fragmentationMetricsService;
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		fragmentationMetricsService.updateBucketCounts(stepExecution.getJobParameters().getString(COLLECTION_NAME));
		return StepExecutionListener.super.afterStep(stepExecution);
	}
}

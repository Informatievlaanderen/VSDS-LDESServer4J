package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.COLLECTION_NAME;

@Component
public class BucketMetricUpdater implements StepExecutionListener {
	private final ServerMetrics serverMetrics;

	public BucketMetricUpdater(ServerMetrics serverMetrics) {
		this.serverMetrics = serverMetrics;
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
		serverMetrics.updateBucketCounts(stepExecution.getJobParameters().getString(COLLECTION_NAME));
		return StepExecutionListener.super.afterStep(stepExecution);
	}
}

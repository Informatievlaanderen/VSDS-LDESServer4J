package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.ContinueFragmentationTriggerEvent;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class FragmentationReadinessListener implements JobExecutionListener {
	private final ApplicationEventPublisher applicationEventPublisher;

	public FragmentationReadinessListener(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		final boolean hasWorkAvailable = jobExecution.getStepExecutions().stream().findFirst().map(StepExecution::getReadCount).map(readCount -> readCount > 0).orElse(true);
		if (hasWorkAvailable && !jobExecution.isRunning()) {
			applicationEventPublisher.publishEvent(new ContinueFragmentationTriggerEvent(jobExecution.getJobParameters()));
		}
	}
}

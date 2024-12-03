package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.LocalDateTime;

public class ContinueFragmentationTriggerEvent {
	final JobParameters originalJobParameters;

	public ContinueFragmentationTriggerEvent(JobParameters originalJobParameters) {
		this.originalJobParameters = originalJobParameters;
	}

	public JobParameters getNewlyTriggeredJobParameters() {
		return new JobParametersBuilder(originalJobParameters).addLocalDateTime("triggered", LocalDateTime.now()).toJobParameters();
	}
}

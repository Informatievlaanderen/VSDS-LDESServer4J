package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaintenanceJobDefinition {
	public static final String MAINTENANCE_JOB = "maintenance";

	@Bean
	public Job maintenanceJob(JobRepository jobRepository,
	                          Step viewRetentionStep,
	                          Step eventSourceRetentionStep,
	                          Step compactionStep,
	                          Step deletionStep) {
		return new JobBuilder(MAINTENANCE_JOB, jobRepository)
				.start(viewRetentionStep)
				.next(eventSourceRetentionStep)
				.next(compactionStep)
				.next(deletionStep)
				.build();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.stepdefinition;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.partitioner.RetentionPolicyPartitioner;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet.EventSourceRetentionTask;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet.ViewRetentionTask;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RetentionStepDefinitions {
	public static final String VIEW_RETENTION_STEP = "viewRetention";
	public static final String EVENT_SOURCE_RETENTION_STEP = "eventSourceRetention";

	@Bean
	public Step viewRetentionStep(JobRepository jobRepository,
	                              PlatformTransactionManager transactionManager,
	                              RetentionPolicyPartitioner viewRetentionPolicyPartitioner,
	                              ViewRetentionTask viewRetentionTask) {
		return new StepBuilder(VIEW_RETENTION_STEP, jobRepository)
				.partitioner("viewRetentionPartitionerStep", viewRetentionPolicyPartitioner)
				.step(new StepBuilder("retentionStep", jobRepository)
						.tasklet(viewRetentionTask, transactionManager)
						.build()
				)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step eventSourceRetentionStep(JobRepository jobRepository,
	                                     PlatformTransactionManager transactionManager,
	                                     RetentionPolicyPartitioner eventSourceRetentionPolicyPartitioner,
	                                     EventSourceRetentionTask eventSourceRetentionTask) {
		return new StepBuilder(EVENT_SOURCE_RETENTION_STEP, jobRepository)
				.partitioner("eventSourceRetentionPartitionerStep", eventSourceRetentionPolicyPartitioner)
				.step(new StepBuilder("retentionStep", jobRepository)
						.tasklet(eventSourceRetentionTask, transactionManager)
						.build()
				)
				.allowStartIfComplete(true)
				.build();
	}
}

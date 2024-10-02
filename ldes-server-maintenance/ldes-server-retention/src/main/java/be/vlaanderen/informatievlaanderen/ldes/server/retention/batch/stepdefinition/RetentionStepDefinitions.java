package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.stepdefinition;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.partitioner.RetentionPolicyPartitioner;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet.EventSourceRetentionTask;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.retentiontasklet.ViewRetentionTask;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RetentionStepDefinitions {
	public static final String VIEW_RETENTION_STEP = "viewRetentionStep";
	public static final String EVENT_SOURCE_RETENTION_STEP = "eventSourceRetentionStep";
	private static final String SINGLE_VIEW_RETENTION_STEP = "singleViewRetentionStep";
	private static final String SINGLE_COLLECTION_RETENTION_STEP = "singleEventSourceRetentionStep";
	private static final String VIEW_PARTITIONER = "viewRetentionPartitioner";
	private static final String COLLECTION_PARTITIONER = "eventSourceRetentionPartitioner";

	@Bean
	public Step viewRetentionStep(JobRepository jobRepository,
	                              RetentionPolicyPartitioner viewRetentionPolicyPartitioner,
	                              ViewRetentionTask viewRetentionTask,
	                              TaskExecutor viewRetentionExecutor,
	                              PlatformTransactionManager transactionManager) {
		return new StepBuilder(VIEW_RETENTION_STEP, jobRepository)
				.partitioner(VIEW_PARTITIONER, viewRetentionPolicyPartitioner)
				.step(new StepBuilder(SINGLE_VIEW_RETENTION_STEP, jobRepository)
						.tasklet(viewRetentionTask, transactionManager)
						.build()
				)
				.taskExecutor(viewRetentionExecutor)
				.build();
	}

	@Bean
	public TaskExecutor viewRetentionExecutor() {
		return new SimpleAsyncTaskExecutor("view_retention_batch");
	}

	@Bean
	public Step eventSourceRetentionStep(JobRepository jobRepository,
	                                     RetentionPolicyPartitioner eventSourceRetentionPolicyPartitioner,
	                                     EventSourceRetentionTask eventSourceRetentionTask,
	                                     TaskExecutor eventSourceRetentionExecutor,
	                                     PlatformTransactionManager transactionManager) {
		return new StepBuilder(EVENT_SOURCE_RETENTION_STEP, jobRepository)
				.partitioner(COLLECTION_PARTITIONER, eventSourceRetentionPolicyPartitioner)
				.step(new StepBuilder(SINGLE_COLLECTION_RETENTION_STEP, jobRepository)
						.tasklet(eventSourceRetentionTask, transactionManager)
						.build()
				)
				.taskExecutor(eventSourceRetentionExecutor)
				.build();
	}

	@Bean
	public TaskExecutor eventSourceRetentionExecutor() {
		return new SimpleAsyncTaskExecutor("event_source_retention_batch");
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CompactionStepDefinitions {
	public static final String COMPACTION_STEP = "compactionStep";
	public static final String SINGLE_VIEW_COMPACTION_STEP = "singleViewCompactionStep";
	public static final String SINGLE_VIEW_COMPACTION_PARTITIONER = "singleViewCompactionPartitioner";
	public static final String DELETION_STEP = "deletionStep";

	@Bean
	public Step compactionStep(JobRepository jobRepository,
	                           Partitioner viewCapacityPartitioner,
	                           Tasklet compactionTask,
	                           PlatformTransactionManager transactionManager) {
		return new StepBuilder(COMPACTION_STEP, jobRepository)
				.partitioner(SINGLE_VIEW_COMPACTION_PARTITIONER, viewCapacityPartitioner)
				.step(new StepBuilder(SINGLE_VIEW_COMPACTION_STEP, jobRepository)
						.tasklet(compactionTask, transactionManager)
						.build())
				.taskExecutor(new SimpleAsyncTaskExecutor("compaction_batch"))
				.build();
	}

	@Bean
	public Step deletionStep(JobRepository jobRepository,
	                         Tasklet pageDeletionTask,
	                         PlatformTransactionManager transactionManager) {
		return new StepBuilder(DELETION_STEP, jobRepository)
				.tasklet(pageDeletionTask, transactionManager)
				.build();
	}
}

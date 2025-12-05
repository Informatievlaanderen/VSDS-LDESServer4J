package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PaginationJobDefinitions {
	public static final String PAGINATION_STEP = "pagination";
	public static final int CHUNK_SIZE = 250;

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           Partitioner bucketPartitioner,
	                           Paginator paginator,
							   PaginationMetricUpdater paginationMetricUpdater,
	                           @Qualifier("paginationTaskExecutor") TaskExecutor taskExecutor) {
		return new StepBuilder(PAGINATION_STEP, jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.tasklet(paginator, transactionManager)
						.build()
				)
				.allowStartIfComplete(true)
				.taskExecutor(taskExecutor)
				.listener(paginationMetricUpdater)
				.build();
	}

	@Bean("paginationTaskExecutor")
	public TaskExecutor paginationTaskExecutor() {
		return new SimpleAsyncTaskExecutor("pagination_batch");
	}
}

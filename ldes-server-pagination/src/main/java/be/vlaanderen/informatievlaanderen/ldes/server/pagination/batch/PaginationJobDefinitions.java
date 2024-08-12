package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class PaginationJobDefinitions {
	public static final String PAGINATION_STEP = "pagination";
	private static final int CHUNK_SIZE = 1000;

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           Partitioner bucketPartitioner, ItemReader<Page> pageItemReader,
	                           ItemProcessor<Page, List<PageAssignment>> pageRelationsProcessor,
	                           ItemWriter<List<PageAssignment>> memberAssigner,
	                           @Qualifier("paginationTaskExecutor") TaskExecutor taskExecutor) {
		return new StepBuilder(PAGINATION_STEP, jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<Page, List<PageAssignment>>chunk(CHUNK_SIZE, transactionManager)
						.reader(pageItemReader)
						.processor(pageRelationsProcessor)
						.writer(memberAssigner)
						.allowStartIfComplete(true)
						.build()
				)
				.allowStartIfComplete(true)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean("paginationTaskExecutor")
	public TaskExecutor paginationTaskExecutor() {
		var taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
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
	public static final String PAGINATION_JOB = "pagination";
	public static final String NEW_VIEW_PAGINATION_JOB = "newViewPagination";
	private static final int CHUNK_SIZE = 1000;

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           Partitioner bucketPartitioner, ItemReader<Page> pageItemReader,
	                           ItemProcessor<Page, List<Page>> pageRelationsProcessor,
	                           ItemWriter<List<Page>> memberAssigner,
	                           @Qualifier("asynch") TaskExecutor taskExecutor) {
		return new StepBuilder("paginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<Page, List<Page>>chunk(CHUNK_SIZE, transactionManager)
						.reader(pageItemReader)
						.processor(pageRelationsProcessor)
						.writer(memberAssigner)
						.allowStartIfComplete(true)
						.taskExecutor(taskExecutor)
						.build()
				)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step newViewPaginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                  Partitioner bucketPartitioner, ItemReader<Page> pageItemReader,
	                                  ItemProcessor<Page, List<Page>> pageRelationsProcessor,
	                                  ItemWriter<List<Page>> memberAssigner,
	                                  @Qualifier("asynch") TaskExecutor taskExecutor) {
		return new StepBuilder("newViewPaginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<Page, List<Page>>chunk(CHUNK_SIZE, transactionManager)
						.reader(pageItemReader)
						.processor(pageRelationsProcessor)
						.writer(memberAssigner)
						.allowStartIfComplete(true)
						.taskExecutor(taskExecutor)
						.build()
				)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean("asynch")
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("spring_batch");
	}
}

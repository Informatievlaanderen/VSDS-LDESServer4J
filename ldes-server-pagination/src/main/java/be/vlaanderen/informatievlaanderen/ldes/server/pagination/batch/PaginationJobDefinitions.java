package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PaginationJobDefinitions {
	public static final String PAGINATION_JOB = "pagination";
	public static final String NEW_VIEW_PAGINATION_JOB = "newViewPagination";

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           Partitioner bucketPartitioner, ItemReader<Page> pageItemReader,
	                           ItemProcessor<Page, Page> pageRelationsProcessor,
	                           ItemWriter<Page> memberAssigner,
	                           TaskExecutor taskExecutor) {
		return new StepBuilder("paginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<Page, Page>chunk(150, transactionManager)
						.reader(pageItemReader)
						.processor(pageRelationsProcessor)
						.writer(memberAssigner)
						.allowStartIfComplete(true)
						.build()
				)
				.taskExecutor(taskExecutor)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step newViewPaginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                  Partitioner bucketPartitioner, ItemReader<Page> pageItemReader,
	                                  ItemProcessor<Page, Page> pageRelationsProcessor,
	                                  ItemWriter<Page> memberAssigner,
	                                  TaskExecutor taskExecutor) {
		return new StepBuilder("newViewPaginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<Page, Page>chunk(150, transactionManager)
						.reader(pageItemReader)
						.processor(pageRelationsProcessor)
						.writer(memberAssigner)
						.allowStartIfComplete(true)
						.build()
				)
				.taskExecutor(taskExecutor)
				.allowStartIfComplete(true)
				.build();
	}
}

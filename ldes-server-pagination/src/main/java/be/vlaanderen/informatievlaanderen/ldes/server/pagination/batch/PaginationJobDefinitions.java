package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
	private PaginationJobDefinitions() {}
	private static final String PAGINATION_JOB = "pagination";
	private static final String NEW_VIEW_PAGINATION_JOB = "newViewPagination";

	@Bean
	public Job paginationJob(JobRepository jobRepository, Step paginationStep) {
		return new JobBuilder(PAGINATION_JOB, jobRepository)
				.start(paginationStep)
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Job newViewPaginationJob(JobRepository jobRepository, Step newViewPaginationStep) {
		return new JobBuilder(NEW_VIEW_PAGINATION_JOB, jobRepository)
				.start(newViewPaginationStep)
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                  Partitioner partitioner, ItemReader<Page> pageItemReader,
	                                  ItemProcessor<Page, Page> pageRelationsProcessor,
	                                  ItemWriter<Page> memberAssigner,
	                                  TaskExecutor taskExecutor) {
		return new StepBuilder("paginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", partitioner)
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
	                                   Partitioner partitioner, ItemReader<Page> pageItemReader,
									   ItemProcessor<Page, Page> pageRelationsProcessor,
									   ItemWriter<Page> memberAssigner,
	                                   TaskExecutor taskExecutor) {
		return new StepBuilder("newViewPaginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", partitioner)
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

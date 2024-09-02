package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.UnpagedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageAssignment;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
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
import org.springframework.transaction.TransactionException;

import java.sql.SQLException;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.COLLECTION_NAME;

@Configuration
public class PaginationJobDefinitions {
	public static final String PAGINATION_STEP = "pagination";
	public static final int CHUNK_SIZE = 250;

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           Partitioner bucketPartitioner, ItemReader<List<UnpagedMember>> pageItemReader,
	                           ItemProcessor<List<UnpagedMember>, List<PageAssignment>> pageRelationsProcessor,
	                           ItemWriter<List<PageAssignment>> memberAssigner,
							   ServerMetrics serverMetrics,
	                           @Qualifier("paginationTaskExecutor") TaskExecutor taskExecutor) {
		return new StepBuilder(PAGINATION_STEP, jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<List<UnpagedMember>, List<PageAssignment>>chunk(1, transactionManager)
						.reader(pageItemReader)
						.processor(pageRelationsProcessor)
						.writer(memberAssigner)
						.faultTolerant()
						.retryLimit(3)
						.retry(SQLException.class)
						.retry(TransactionException.class)
						.build()
				)
				.allowStartIfComplete(true)
				.taskExecutor(taskExecutor)
				.listener(new StepExecutionListener() {
					@Override
					public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
						serverMetrics.updatePaginationCounts(stepExecution.getJobParameters().getString(COLLECTION_NAME));
						return StepExecutionListener.super.afterStep(stepExecution);
					}
				})
				.build();
	}

	@Bean("paginationTaskExecutor")
	public TaskExecutor paginationTaskExecutor() {
		var taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
		taskExecutor.setConcurrencyLimit(5);
		return taskExecutor;
	}
}

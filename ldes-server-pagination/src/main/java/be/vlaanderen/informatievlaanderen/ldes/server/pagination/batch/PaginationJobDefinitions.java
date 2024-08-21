package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.UnpagedMember;
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
import org.springframework.transaction.TransactionException;

import java.sql.SQLException;
import java.util.List;

@Configuration
public class PaginationJobDefinitions {
	public static final String PAGINATION_STEP = "pagination";
	public static final int CHUNK_SIZE = 250;

	@Bean
	public Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           Partitioner bucketPartitioner, ItemReader<List<UnpagedMember>> pageItemReader,
	                           ItemProcessor<List<UnpagedMember>, List<PageAssignment>> pageRelationsProcessor,
	                           ItemWriter<List<PageAssignment>> memberAssigner,
	                           @Qualifier("paginationTaskExecutor") TaskExecutor taskExecutor) {
		return new StepBuilder(PAGINATION_STEP, jobRepository)
				.partitioner("memberBucketPartitionStep", bucketPartitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<List<UnpagedMember>, List<PageAssignment>>chunk(CHUNK_SIZE, transactionManager)
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
				.build();
	}

	@Bean("paginationTaskExecutor")
	public TaskExecutor paginationTaskExecutor() {
		var taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
		taskExecutor.setConcurrencyLimit(5);
		return taskExecutor;
	}
}

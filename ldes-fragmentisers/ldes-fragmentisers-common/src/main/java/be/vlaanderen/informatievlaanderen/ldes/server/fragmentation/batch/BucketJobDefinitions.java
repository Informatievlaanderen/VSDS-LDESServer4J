package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
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
public class BucketJobDefinitions {
	public static final String BUCKETISATION_STEP = "bucketisation";
	public static final int CHUNK_SIZE = 250;

	@Bean(BUCKETISATION_STEP)
	public Step bucketiseMembersStep(JobRepository jobRepository,
	                                 PlatformTransactionManager transactionManager,
	                                 ItemReader<FragmentationMember> memberReader,
	                                 ItemProcessor<FragmentationMember, List<BucketisedMember>> viewBucketProcessor,
	                                 ItemWriter<List<BucketisedMember>> writer,
	                                 ServerMetrics serverMetrics,
	                                 @Qualifier("bucketTaskExecutor") TaskExecutor taskExecutor) {
		return new StepBuilder(BUCKETISATION_STEP, jobRepository)
				.<FragmentationMember, List<BucketisedMember>>chunk(CHUNK_SIZE, transactionManager)
				.reader(memberReader)
				.processor(viewBucketProcessor)
				.writer(writer)
				.listener(new StepExecutionListener() {
					@Override
					public ExitStatus afterStep(@NotNull StepExecution stepExecution) {
						serverMetrics.updateBucketCounts(stepExecution.getJobParameters().getString("collectionName"));
						return StepExecutionListener.super.afterStep(stepExecution);
					}
				})
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean("bucketTaskExecutor")
	public TaskExecutor paginationTaskExecutor() {
		var taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}
}

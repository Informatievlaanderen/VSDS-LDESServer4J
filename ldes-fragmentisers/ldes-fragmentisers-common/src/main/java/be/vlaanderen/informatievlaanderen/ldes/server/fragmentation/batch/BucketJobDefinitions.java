package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import org.springframework.batch.core.Step;
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
	// TODO: verify if this needs to be changed
	public static final int CHUNK_SIZE = 250;

	@Bean(BUCKETISATION_STEP)
	public Step bucketiseMembersStep(JobRepository jobRepository,
	                                 PlatformTransactionManager transactionManager,
	                                 ItemReader<FragmentationMember> memberReader,
	                                 ItemProcessor<FragmentationMember, List<BucketisedMember>> viewBucketProcessor,
	                                 ItemWriter<List<BucketisedMember>> writer,
	                                 BucketMetricUpdater bucketMetricUpdater,
	                                 @Qualifier("bucketTaskExecutor") TaskExecutor taskExecutor) {
		return new StepBuilder(BUCKETISATION_STEP, jobRepository)
				.<FragmentationMember, List<BucketisedMember>>chunk(CHUNK_SIZE, transactionManager)
				.reader(memberReader)
				.processor(viewBucketProcessor)
				.writer(writer)
				.listener(bucketMetricUpdater)
				.build();
	}

	@Bean("bucketTaskExecutor")
	public TaskExecutor paginationTaskExecutor() {
		var taskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
		// TODO: higher this limit: Jan will help me with this, as he wants this dynamically
		taskExecutor.setConcurrencyLimit(1);
		return taskExecutor;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BucketStepDefinitions {
	public static final String BUCKETISATION_STEP = "bucketisation";
	public static final int CHUNK_SIZE = 250;

	@Bean(BUCKETISATION_STEP)
	public Step bucketiseMembersStep(JobRepository jobRepository,
	                                 PlatformTransactionManager transactionManager,
	                                 ItemReader<FragmentationMember> memberReader,
	                                 ItemProcessor<FragmentationMember, Bucket> bucketProcessor,
	                                 ItemWriter<Bucket> bucketisationItemWriter,
	                                 BucketMetricUpdater bucketMetricUpdater) {
		return new StepBuilder(BUCKETISATION_STEP, jobRepository)
				.<FragmentationMember, Bucket>chunk(CHUNK_SIZE, transactionManager)
				.reader(memberReader)
				.processor(bucketProcessor)
				.writer(bucketisationItemWriter)
				.listener(bucketMetricUpdater)
				.build();
	}

}

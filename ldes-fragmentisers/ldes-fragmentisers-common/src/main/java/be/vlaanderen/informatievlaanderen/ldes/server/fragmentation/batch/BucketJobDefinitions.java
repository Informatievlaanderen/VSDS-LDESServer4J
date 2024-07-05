package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class BucketJobDefinitions {
	public static final String BUCKETISATION_JOB = "bucketisation";
	public static final String REBUCKETISATION_JOB = "rebucketisation";

	@Bean
	public Job bucketiseJob(JobRepository jobRepository, Step bucketiseMembersStep) {
		return new JobBuilder(BUCKETISATION_JOB, jobRepository)
				.start(bucketiseMembersStep)
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Job rebucketiseJob(JobRepository jobRepository, Step rebucketiseMembersStep) {
		return new JobBuilder(REBUCKETISATION_JOB, jobRepository)
				.start(rebucketiseMembersStep)
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Step bucketiseMembersStep(JobRepository jobRepository,
	                                  PlatformTransactionManager transactionManager,
	                                  ItemReader<FragmentationMember> newMemberReader, BucketProcessor processor,
	                                  ItemWriter<List<BucketisedMember>> writer) {
		return new StepBuilder("bucketiseMembers", jobRepository)
				.<FragmentationMember, List<BucketisedMember>>chunk(150, transactionManager)
				.reader(newMemberReader)
				.processor(processor)
				.writer(writer)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step rebucketiseMembersStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                   ItemReader<FragmentationMember> refragmentEventStream, BucketProcessor processor,
	                                   ItemWriter<List<BucketisedMember>> writer) {
		return new StepBuilder("rebucketiseMembers", jobRepository)
				.<FragmentationMember, List<BucketisedMember>>chunk(150, transactionManager)
				.reader(refragmentEventStream)
				.processor(processor)
				.writer(writer)
				.allowStartIfComplete(true)
				.build();
	}
}

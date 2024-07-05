package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Component
public class BucketJobDefinitions {
	private BucketJobDefinitions() {}
	public static final String BUCKETISATION_JOB = "bucketisation";
	public static final String REBUCKETISATION_JOB = "rebucketisation";

	public static Job bucketiseJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                         ItemReader<IngestedMember> reader, BucketProcessor processor,
	                         ItemWriter<List<BucketisedMember>> writer) {
		return new JobBuilder(BUCKETISATION_JOB, jobRepository)
				.start(bucketiseMembers(jobRepository, transactionManager, reader, processor, writer))
				.incrementer(new RunIdIncrementer())
				.build();
	}

	public static Job rebucketiseJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                           ItemReader<IngestedMember> reader, BucketProcessor processor,
	                           ItemWriter<List<BucketisedMember>> writer) {
		return new JobBuilder(REBUCKETISATION_JOB, jobRepository)
				.start(rebucketiseMembers(jobRepository, transactionManager, reader, processor, writer))
				.incrementer(new RunIdIncrementer())
				.build();
	}

	private static Step bucketiseMembers(JobRepository jobRepository,
	                              PlatformTransactionManager transactionManager,
	                              ItemReader<IngestedMember> reader, BucketProcessor processor,
	                              ItemWriter<List<BucketisedMember>> writer) {
		return new StepBuilder("bucketiseMembers", jobRepository)
				.<IngestedMember, List<BucketisedMember>>chunk(150, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.allowStartIfComplete(true)
				.build();
	}

	private static Step rebucketiseMembers(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                ItemReader<IngestedMember> reader, BucketProcessor processor,
	                                ItemWriter<List<BucketisedMember>> writer) {
		return new StepBuilder("rebucketiseMembers", jobRepository)
				.<IngestedMember, List<BucketisedMember>>chunk(150, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.allowStartIfComplete(true)
				.build();
	}
}

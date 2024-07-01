package be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

public class PaginationJobDefinitions {
	private PaginationJobDefinitions() {}
	private static final String PAGINATION_JOB = "pagination";
	private static final String NEW_VIEW_PAGINATION_JOB = "newViewPagination";

	public static Job paginationJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                          Partitioner partitioner, ItemReader<List<BucketisedMember>> reader,
	                          PaginationProcessor processor, ItemWriter<List<MemberAllocation>> writer,
	                          TaskExecutor taskExecutor) {
		return new JobBuilder(PAGINATION_JOB, jobRepository)
				.start(paginationStep(jobRepository, transactionManager, partitioner, reader, processor,
						writer, taskExecutor))
				.incrementer(new RunIdIncrementer())
				.build();
	}

	public static Job newViewPaginationJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                 Partitioner partitioner, ItemReader<List<BucketisedMember>> reader,
	                                 PaginationProcessor processor, ItemWriter<List<MemberAllocation>> writer,
	                                 TaskExecutor taskExecutor) {
		return new JobBuilder(NEW_VIEW_PAGINATION_JOB, jobRepository)
				.start(newViewPaginationStep(jobRepository, transactionManager, partitioner, reader, processor,
						writer, taskExecutor))
				.incrementer(new RunIdIncrementer())
				.build();
	}

	private static Step paginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                            Partitioner partitioner, ItemReader<List<BucketisedMember>> reader,
	                            PaginationProcessor processor, ItemWriter<List<MemberAllocation>> writer,
	                            TaskExecutor taskExecutor) {
		return new StepBuilder("paginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", partitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<List<BucketisedMember>, List<MemberAllocation>>chunk(150, transactionManager)
						.reader(reader)
						.processor(processor)
						.writer(writer)
						.allowStartIfComplete(true)
						.build()
				)
				.taskExecutor(taskExecutor)
				.allowStartIfComplete(true)
				.build();
	}

	private static Step newViewPaginationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
	                                   Partitioner partitioner, ItemReader<List<BucketisedMember>> reader,
	                                   PaginationProcessor processor, ItemWriter<List<MemberAllocation>> writer,
	                                   TaskExecutor taskExecutor) {
		return new StepBuilder("newViewPaginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", partitioner)
				.step(new StepBuilder("paginationStep", jobRepository)
						.<List<BucketisedMember>, List<MemberAllocation>>chunk(150, transactionManager)
						.reader(reader)
						.processor(processor)
						.writer(writer)
						.allowStartIfComplete(true)
						.build()
				)
				.taskExecutor(taskExecutor)
				.allowStartIfComplete(true)
				.build();
	}
}

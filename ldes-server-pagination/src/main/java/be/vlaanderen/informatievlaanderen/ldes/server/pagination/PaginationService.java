package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewRebucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaginationService {
	private static final Logger log = LoggerFactory.getLogger(PaginationService.class);
	private static final String PAGINATION_JOB = "pagination";
	private static final String NEW_VIEW_PAGINATION_JOB = "newViewPagination";
	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final Partitioner bucketisationPartitioner;
	private final Partitioner rebucketisationPartitioner;
	private final ItemReader<List<BucketisedMember>> reader;
	private final PaginationProcessor processor;
	private final ItemWriter<List<MemberAllocation>> writer;
	private final TaskExecutor taskExecutor;
	private final JobExplorer jobExplorer;
	private boolean shouldTriggerPagination;
	private boolean shouldTriggerNewViewPagination;

	public PaginationService(JobLauncher jobLauncher, JobRepository jobRepository,
	                         PlatformTransactionManager transactionManager,
	                         @Qualifier("bucketisationPartitioner") Partitioner bucketisationPartitioner,
	                         @Qualifier("rebucketisationPartitioner") Partitioner rebucketisationPartitioner,
	                         ItemReader<List<BucketisedMember>> reader, PaginationProcessor processor, ItemWriter<List<MemberAllocation>> writer, TaskExecutor taskExecutor, JobExplorer jobExplorer) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.bucketisationPartitioner = bucketisationPartitioner;
		this.rebucketisationPartitioner = rebucketisationPartitioner;
		this.reader = reader;
		this.processor = processor;
		this.writer = writer;
		this.taskExecutor = taskExecutor;
		this.jobExplorer = jobExplorer;
	}


	@EventListener
	@SuppressWarnings("java:S2629")
	public void handleMemberBucketisedEvent(MembersBucketisedEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		if (isJobRunning(PAGINATION_JOB) || isJobRunning(NEW_VIEW_PAGINATION_JOB)) {
			shouldTriggerPagination = true;
		} else {
			runJob(paginationJob(), new JobParametersBuilder()
					.addLocalDateTime("triggered", LocalDateTime.now())
					.toJobParameters());
		}
	}

	@EventListener
	@SuppressWarnings("java:S2629")
	public void handleMemberBucketisedEvent(ViewRebucketisedEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		runJob(newViewPaginationJob(), new JobParametersBuilder()
				.addString("viewName", event.viewName())
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters());
	}

	private boolean isJobRunning(String jobName) {
		return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
	}

	private void runJob(Job job, JobParameters jobParameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(job, jobParameters);
		if (job.getName().equals(PAGINATION_JOB) && shouldTriggerPagination) {
			shouldTriggerPagination = false;
			runJob(job, jobParameters);
		} else if (job.getName().equals(PAGINATION_JOB) && shouldTriggerPagination) {
			shouldTriggerPagination = false;
			runJob(paginationJob(), new JobParametersBuilder()
					.addLocalDateTime("triggered", LocalDateTime.now())
					.toJobParameters());
		}

	}

	private Job paginationJob() {
		return new JobBuilder(PAGINATION_JOB, jobRepository)
				.start(paginationStep())
				.build();
	}

	private Job newViewPaginationJob() {
		return new JobBuilder(NEW_VIEW_PAGINATION_JOB, jobRepository)
				.start(newViewPaginationStep())
				.build();
	}

	private Step paginationStep() {
		return new StepBuilder("paginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", bucketisationPartitioner)
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

	private Step newViewPaginationStep() {
		return new StepBuilder("newViewPaginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitionStep", rebucketisationPartitioner)
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

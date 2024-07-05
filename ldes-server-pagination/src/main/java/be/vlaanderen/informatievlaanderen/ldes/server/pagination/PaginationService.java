package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@EnableScheduling
public class PaginationService {
	private static final String PAGINATION_JOB = "pagination";
	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final TaskExecutor taskExecutor;
	private final JobExplorer jobExplorer;
	private final Partitioner bucketPartitioner;
	private final ItemReader<Page> pageReader;
	private final ItemProcessor<Page, Page> pageRelationsProcessor;
	private final ItemWriter<Page> memberAssigner;
	private boolean shouldTriggerPagination = true;


	public PaginationService(JobLauncher jobLauncher,
	                         JobRepository jobRepository,
	                         PlatformTransactionManager transactionManager,
	                         TaskExecutor taskExecutor,
	                         JobExplorer jobExplorer,
	                         Partitioner bucketPartitioner,
	                         ItemReader<Page> pageReader,
	                         ItemProcessor<Page, Page> pageRelationsProcessor,
	                         ItemWriter<Page> memberAssigner
	) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.taskExecutor = taskExecutor;
		this.jobExplorer = jobExplorer;
		this.bucketPartitioner = bucketPartitioner;
		this.pageReader = pageReader;
		this.pageRelationsProcessor = pageRelationsProcessor;
		this.memberAssigner = memberAssigner;
	}

	@Scheduled(fixedRate = 1500)
	public void scheduledJobLauncher() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		if (shouldTriggerPagination && noJobRunning()) {
			jobLauncher.run(paginationJob(), new JobParametersBuilder().toJobParameters());
		}
	}

	@EventListener(MembersBucketisedEvent.class)
	public void handleMemberBucketisedEvent() {
		shouldTriggerPagination = true;
	}


	private boolean noJobRunning() {
		return jobExplorer.findRunningJobExecutions(PAGINATION_JOB).isEmpty();
	}

	private Job paginationJob() {
		return new JobBuilder(PAGINATION_JOB, jobRepository)
				.start(paginationMasterStep())
				.incrementer(new RunIdIncrementer())
				.build();

	}

	private Step paginationMasterStep() {
		return new StepBuilder("paginationMasterStep", jobRepository)
				.partitioner("memberBucketPartitioner", bucketPartitioner)
				.partitionHandler(partitionHandler())
				.listener(new StepExecutionListener() {
					@Override
					public void beforeStep(@NotNull StepExecution stepExecution) {
//						shouldTriggerPagination = stepExecution.getExecutionContext().isEmpty();
					}
				})
				.allowStartIfComplete(true)
				.build();
	}

	private PartitionHandler partitionHandler() {
		final TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
		taskExecutorPartitionHandler.setStep(paginationStep());
		taskExecutorPartitionHandler.setGridSize(5);
		taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
		return taskExecutorPartitionHandler;
	}

	private Step paginationStep() {
		return new StepBuilder("paginationStep", jobRepository)
				.<Page, Page>chunk(150, transactionManager)
				.reader(pageReader)
				.processor(pageRelationsProcessor)
				.writer(memberAssigner)
				.allowStartIfComplete(true)
				.build();
	}

}

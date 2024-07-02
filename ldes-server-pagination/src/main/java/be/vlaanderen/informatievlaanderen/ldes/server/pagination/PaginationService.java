package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.NewViewBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationJobDefinitions.newViewPaginationJob;
import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationJobDefinitions.paginationJob;

@Component
@EnableScheduling
public class PaginationService {

	private final JobLauncher jobLauncher;
	private final Job paginationJob;
	private final Job newViewPaginationJob;

	public PaginationService(JobLauncher jobLauncher, JobRepository jobRepository,
	                         PlatformTransactionManager transactionManager,
	                         @Qualifier("bucketisationPartitioner") Partitioner bucketisationPartitioner,
	                         @Qualifier("viewBucketisationPartitioner") Partitioner viewBucketisationPartitioner,
	                         ItemReader<List<BucketisedMember>> reader, PaginationProcessor processor,
	                         ItemWriter<List<MemberAllocation>> writer, TaskExecutor taskExecutor) {
		this.jobLauncher = jobLauncher;
		this.newViewPaginationJob = newViewPaginationJob(jobRepository, transactionManager, viewBucketisationPartitioner,
				reader, processor, writer, taskExecutor);
		this.paginationJob = paginationJob(jobRepository, transactionManager, bucketisationPartitioner, reader, processor,
				writer, taskExecutor);
	}

	@EventListener
	@SuppressWarnings("java:S2629")
	public void handleNewViewBucketisedEvent(NewViewBucketisedEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(newViewPaginationJob, new JobParametersBuilder()
				.addString("viewName", event.viewName())
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters());
	}

	@EventListener(MembersBucketisedEvent.class)
	public void handleMemberBucketisedEvent() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(paginationJob, new JobParametersBuilder()
				.toJobParameters());
	}

}

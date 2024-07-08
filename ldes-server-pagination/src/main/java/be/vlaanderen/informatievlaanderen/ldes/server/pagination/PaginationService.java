package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.NewViewBucketisedEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationJobDefinitions.NEW_VIEW_PAGINATION_JOB;
import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.batch.PaginationJobDefinitions.PAGINATION_JOB;

@Component
@EnableScheduling
public class PaginationService {

	private final JobLauncher jobLauncher;
	private final Job paginationJob;
	private final Job newViewPaginationJob;

	public PaginationService(JobLauncher jobLauncher,
							 JobRepository jobRepository,
							 Step paginationStep,
							 Step newViewPaginationStep) {
		this.jobLauncher = jobLauncher;
		this.paginationJob = createJob(PAGINATION_JOB, jobRepository, paginationStep);
		this.newViewPaginationJob = createJob(NEW_VIEW_PAGINATION_JOB, jobRepository, newViewPaginationStep);
	}

	@EventListener
	public void handleNewViewBucketisedEvent(NewViewBucketisedEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(newViewPaginationJob, new JobParametersBuilder()
				.addString("viewName", event.viewName())
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters());
	}

	@EventListener(MembersBucketisedEvent.class)
	public void handleMemberBucketisedEvent() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(paginationJob, new JobParametersBuilder().toJobParameters());
	}

	private Job createJob(String jobName, JobRepository jobRepository, Step step) {
		return new JobBuilder(jobName, jobRepository)
				.start(step)
				.incrementer(new RunIdIncrementer())
				.build();
	}
}

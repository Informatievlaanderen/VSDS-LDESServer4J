package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.NewViewBucketisedEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class PaginationService {

	private final JobLauncher jobLauncher;
	private final Job paginationJob;
	private final Job newViewPaginationJob;

	public PaginationService(JobLauncher jobLauncher,
	                         Job newViewPaginationJob,
	                         Job paginationJob) {
		this.jobLauncher = jobLauncher;
		this.newViewPaginationJob = newViewPaginationJob;
		this.paginationJob = paginationJob;
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

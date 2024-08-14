package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.MemberMetricsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.FRAGMENTATION_CRON;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketJobDefinitions.BUCKETISATION_STEP;

@Service
@EnableScheduling
public class FragmentationService {
	public static final String FRAGMENTATION_JOB = "fragmentation";
	public static final String REFRAGMENTATION_JOB = "refragmentation";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW_NAME = "viewName";
	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final JobRepository jobRepository;
	private final Job bucketiseJob;
	private final ServerMetrics serverMetrics;
	private final MemberMetricsRepository memberRepository;

	public FragmentationService(JobLauncher jobLauncher, JobRepository jobRepository, JobExplorer jobExplorer,
	                            @Qualifier(BUCKETISATION_STEP) Step bucketiseMembersStep,
	                            Step paginationStep,
	                            ServerMetrics serverMetrics,
	                            MemberMetricsRepository memberRepository) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.jobRepository = jobRepository;
		this.serverMetrics = serverMetrics;
		this.memberRepository = memberRepository;
		this.bucketiseJob = createJob(FRAGMENTATION_JOB, jobRepository, bucketiseMembersStep, paginationStep);
		this.cleanupOldJobs();
	}

	@Scheduled(cron = FRAGMENTATION_CRON)
	public void scheduledJobLauncher() {
		if (noJobsRunning(FRAGMENTATION_JOB) && noJobsRunning(REFRAGMENTATION_JOB)) {
			memberRepository.getUnprocessedCollections()
					.parallelStream()
					.forEach(viewName -> {
						try {
							launchJob(bucketiseJob, new JobParametersBuilder()
									.addString(VIEW_NAME, viewName.getViewName())
									.addString(COLLECTION_NAME, viewName.getCollectionName())
									.addLocalDateTime("triggered", LocalDateTime.now())
									.toJobParameters());
						} catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
						         JobParametersInvalidException | JobRestartException e) {
							throw new RuntimeException(e);
						}
					});
		}
	}

	private void launchJob(Job job, JobParameters jobParameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(job, jobParameters);

		serverMetrics.updatePaginationCounts(jobParameters.getString(COLLECTION_NAME));
	}

	private boolean noJobsRunning(String jobName) {
		return jobExplorer.findRunningJobExecutions(jobName).isEmpty();
	}

	private Job createJob(String jobName, JobRepository jobRepository, Step step, Step paginationStep) {
		return new JobBuilder(jobName, jobRepository)
				.start(step)
				.next(paginationStep)
				.build();
	}

	public void cleanupOldJobs() {
		jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB).forEach(this::stopJob);
	}

	private void stopJob(JobExecution jobExecution) {
		jobExecution.setStatus(BatchStatus.ABANDONED);
		jobExecution.setEndTime(LocalDateTime.now());
		jobRepository.update(jobExecution);
	}
}

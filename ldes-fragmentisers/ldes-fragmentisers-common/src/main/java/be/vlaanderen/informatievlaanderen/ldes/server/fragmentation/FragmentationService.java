package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.UnprocessedView;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.FragmentationJobException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.UnprocessedViewRepository;
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
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.FRAGMENTATION_CRON;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BatchConfiguration.ASYNC_JOB_LAUNCHER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketJobDefinitions.BUCKETISATION_STEP;

@Service
@EnableScheduling
public class FragmentationService {
	public static final String FRAGMENTATION_JOB = "fragmentation";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW_NAME = "viewName";
	public static final String COLLECTION_ID = "collectionId";
	public static final String VIEW_ID = "viewId";
	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final JobRepository jobRepository;
	private final Job bucketiseJob;
	private final UnprocessedViewRepository unprocessedViewRepository;

	public FragmentationService(@Qualifier(ASYNC_JOB_LAUNCHER) JobLauncher jobLauncher, JobRepository jobRepository, JobExplorer jobExplorer,
	                            @Qualifier(BUCKETISATION_STEP) Step bucketiseMembersStep, Step paginationStep,
	                            UnprocessedViewRepository unprocessedViewRepository) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.jobRepository = jobRepository;
		this.unprocessedViewRepository = unprocessedViewRepository;
		this.bucketiseJob = createJob(bucketiseMembersStep, paginationStep);
		this.cleanupOldJobs();
	}

	@Scheduled(cron = FRAGMENTATION_CRON)
	public void scheduledJobLauncher() {
		unprocessedViewRepository.findAll()
				.parallelStream()
				.filter(this::noJobsRunning)
				.forEach(unprocessedView -> {
					try {
						jobLauncher.run(bucketiseJob, new JobParametersBuilder()
								.addLong(VIEW_ID, (long) unprocessedView.viewId())
								.addLong(COLLECTION_ID, (long) unprocessedView.collectionId())
								.addString(VIEW_NAME, unprocessedView.viewName())
								.addString(COLLECTION_NAME, unprocessedView.collectionName())
								.addLocalDateTime("triggered", LocalDateTime.now())
								.toJobParameters());
					} catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
					         JobParametersInvalidException | JobRestartException e) {
						throw new FragmentationJobException(e);
					}
				});
	}

	private boolean noJobsRunning(UnprocessedView unprocessedView) {
		return jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB)
				.stream()
				.noneMatch(jobExecution -> {
					var params = jobExecution.getJobParameters();
					final UnprocessedView fromParams = new UnprocessedView(
							Objects.requireNonNull(params.getLong(COLLECTION_ID)).intValue(),
							params.getString(COLLECTION_NAME),
							Objects.requireNonNull(params.getLong(VIEW_ID)).intValue(),
							params.getString(VIEW_NAME)
					);
					return Objects.equals(fromParams, unprocessedView);
				});
	}

	private Job createJob(Step step, Step paginationStep) {
		return new JobBuilder(FRAGMENTATION_JOB, jobRepository)
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

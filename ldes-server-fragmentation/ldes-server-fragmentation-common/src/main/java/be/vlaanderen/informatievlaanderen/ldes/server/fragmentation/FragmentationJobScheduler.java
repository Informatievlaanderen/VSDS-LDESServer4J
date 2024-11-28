package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.UnprocessedView;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.FragmentationJobException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.UnprocessedViewRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.FRAGMENTATION_CRON;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BatchConfiguration.ASYNC_JOB_LAUNCHER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.FragmentationJobDefintions.FRAGMENTATION_JOB;

@Service
@EnableScheduling
public class FragmentationJobScheduler {
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW_NAME = "viewName";
	public static final String COLLECTION_ID = "collectionId";
	public static final String VIEW_ID = "viewId";
	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job fragmentationJob;
	private final UnprocessedViewRepository unprocessedViewRepository;

	public FragmentationJobScheduler(@Qualifier(ASYNC_JOB_LAUNCHER) JobLauncher jobLauncher,
	                                 JobExplorer jobExplorer,
	                                 SimpleJobBuilder fragmentationJobBuilder,
	                                 UnprocessedViewRepository unprocessedViewRepository) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.unprocessedViewRepository = unprocessedViewRepository;
		this.fragmentationJob = fragmentationJobBuilder.build();
	}

	@Scheduled(cron = FRAGMENTATION_CRON)
	public void scheduleJobs() {
		unprocessedViewRepository.findAll()
				.parallelStream()
				.filter(this::noJobsRunning)
				.forEach(unprocessedView -> {
					try {
						jobLauncher.run(fragmentationJob, new JobParametersBuilder()
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
}

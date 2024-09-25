package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.exceptions.MaintenanceJobException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.RETENTION_CRON_KEY;

@Service
@EnableScheduling
public class MaintenanceService {
	public static final String MAINTENANCE_JOB = "maintenance";
	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final Job maintenanceJob;

	public MaintenanceService(JobLauncher jobLauncher,
	                          JobRepository jobRepository,
	                          Step viewRetentionStep,
	                          Step eventSourceRetentionStep) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		maintenanceJob = createJob(viewRetentionStep, eventSourceRetentionStep);
	}

	@Scheduled(cron = RETENTION_CRON_KEY)
	public void scheduleMaintenanceJob() {
		try {
			jobLauncher.run(maintenanceJob, new JobParametersBuilder()
					.addLocalDateTime("triggered", LocalDateTime.now())
					.toJobParameters());
		} catch (JobExecutionException e) {
			throw new MaintenanceJobException(e);
		}
	}

	private Job createJob(Step viewRetentionStep,  Step eventSourceRetentionStep) {
		return new JobBuilder(MAINTENANCE_JOB, jobRepository)
				.start(viewRetentionStep)
				.next(eventSourceRetentionStep)
				.build();
	}
}

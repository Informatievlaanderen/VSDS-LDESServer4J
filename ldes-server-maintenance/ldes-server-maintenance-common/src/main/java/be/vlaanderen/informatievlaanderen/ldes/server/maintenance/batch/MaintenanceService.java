package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.exceptions.MaintenanceJobException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.RETENTION_CRON_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch.MaintenanceFlows.MAINTENANCE_JOB;

@Service
@EnableScheduling
public class MaintenanceService {
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job maintenanceJob;

	public MaintenanceService(JobLauncher jobLauncher,
	                          JobExplorer jobExplorer,
	                          FlowJobBuilder maintenanceJobBuilder) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.maintenanceJob = maintenanceJobBuilder.build();
	}

	//TODO: change cron key
	@Scheduled(cron = RETENTION_CRON_KEY)
	public void scheduleMaintenanceJob() {
		try {
			if (hasNoJobsRunning()) {
				jobLauncher.run(maintenanceJob, new JobParametersBuilder()
						.addLocalDateTime("triggered", LocalDateTime.now())
						.toJobParameters());
			}
		} catch (JobExecutionException e) {
			throw new MaintenanceJobException(e);
		}
	}

	private boolean hasNoJobsRunning() {
		return jobExplorer.findRunningJobExecutions(MAINTENANCE_JOB).isEmpty();
	}
}

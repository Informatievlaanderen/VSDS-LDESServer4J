package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.exceptions.MaintenanceJobException;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.services.RetentionPolicyEmptinessChecker;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.RETENTION_CRON_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch.MaintenanceJobDefinition.MAINTENANCE_JOB;

@Service
@EnableScheduling
public class MaintenanceService {
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final Job maintenanceJob;
	private final RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker;

	public MaintenanceService(JobLauncher jobLauncher,
	                          JobExplorer jobExplorer,
	                          Job maintenanceJob,
	                          RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.maintenanceJob = maintenanceJob;
		this.retentionPolicyEmptinessChecker = retentionPolicyEmptinessChecker;
	}

	//TODO: change cron key
	@Scheduled(cron = RETENTION_CRON_KEY)
	public void scheduleMaintenanceJob() {
		if(retentionPolicyEmptinessChecker.isEmpty()) {
			return;
		}
		try {
			if(hasNoJobsRunning()) {
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

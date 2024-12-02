package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.FragmentationJobDefinitions.FRAGMENTATION_JOB;

@Component
public class OldJobsAbandoner implements CommandLineRunner {
	private final JobRepository jobRepository;
	private final JobExplorer jobExplorer;

	public OldJobsAbandoner(JobRepository jobRepository, JobExplorer jobExplorer) {
		this.jobRepository = jobRepository;
		this.jobExplorer = jobExplorer;
	}

	@Override
	public void run(String... args) {
		jobExplorer.findRunningJobExecutions(FRAGMENTATION_JOB).forEach(this::abandonJob);
	}

	private void abandonJob(JobExecution jobExecution) {
		jobExecution.setStatus(BatchStatus.ABANDONED);
		jobExecution.setEndTime(LocalDateTime.now());
		jobRepository.update(jobExecution);
	}
}

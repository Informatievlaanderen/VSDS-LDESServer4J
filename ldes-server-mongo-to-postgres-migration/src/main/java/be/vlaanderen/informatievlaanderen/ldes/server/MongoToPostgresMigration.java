package be.vlaanderen.informatievlaanderen.ldes.server;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableBatchProcessing
@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
public class MongoToPostgresMigration {

	@Autowired
	private JobLauncher jobLauncher;

	@Qualifier("migrationMongoIngest")
	@Autowired
	private Job ingestMigration;

	@Qualifier("migrationMongoFetch")
	@Autowired
	private Job fetchMigration;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			List<Job> migrationJobs = List.of(ingestMigration, fetchMigration);
			for (Job job : migrationJobs) {
				jobLauncher.run(job, new JobParameters());
			}
		};
	}

}

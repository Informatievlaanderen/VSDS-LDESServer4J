package be.vlaanderen.informatievlaanderen.ldes.server;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test")
@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
public class MongoToPostgresMigration {

	private final JobLauncher jobLauncher;
	private final List<Job> migrationJobs;

	public MongoToPostgresMigration(JobLauncher jobLauncher,
	                                @Qualifier("migrationMongoIngest") Job ingestMigration,
	                                @Qualifier("migrationMongoFetch") Job fetchMigration,
	                                @Qualifier("migrationMongoFragmentation") Job fragmentMigration,
	                                @Qualifier("migrationMongoRetention") Job retentionMigration,
	                                @Qualifier("migrationMongoAdmin") Job adminMigration) {
		this.jobLauncher = jobLauncher;
		this.migrationJobs = List.of(ingestMigration, fetchMigration, fragmentMigration,
				retentionMigration, adminMigration);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			for (Job job : migrationJobs) {
				jobLauncher.run(job, new JobParameters());
			}
		};
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.admin;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
public class AdminMigrationConfig {

	@Bean("migrationMongoAdmin")
	public Job memberEntityMigrationJob(JobRepository jobRepository,
	                                    @Qualifier("migrationDcatDataset") Step dcatDatasetStep,
	                                    @Qualifier("migrationDcatCatalog") Step dcatCatalogStep,
                                        @Qualifier("migrationEventStream") Step eventStreamStep,
                                        @Qualifier("migrationShaclShape") Step shaclShapeStep,
                                        @Qualifier("migrationDataService") Step dataServiceStep,
                                        @Qualifier("migrationView") Step viewStep
	) {

		return new JobBuilder("migrationMongoAdmin", jobRepository)
				.incrementer(new RunIdIncrementer())
				.flow(dcatDatasetStep)
				.next(dcatCatalogStep)
				.next(eventStreamStep)
				.next(shaclShapeStep)
				.next(dataServiceStep)
				.next(viewStep)
				.end()
				.build();
	}
}

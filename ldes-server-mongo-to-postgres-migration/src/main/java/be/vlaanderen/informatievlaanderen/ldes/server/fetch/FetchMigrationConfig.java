package be.vlaanderen.informatievlaanderen.ldes.server.fetch;


import be.vlaanderen.informatievlaanderen.ldes.server.postgres.fetch.entity.MemberAllocationEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class FetchMigrationConfig {
	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fetch.MemberAllocationEntity> reader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fetch.MemberAllocationEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.fetch.MemberAllocationEntity.class);
		reader.setSort(new HashMap<>() {{
			put("_id", Sort.Direction.DESC);
		}});
		reader.setQuery("{}");
		reader.setPageSize(1000);
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fetch.MemberAllocationEntity, MemberAllocationEntity> dataProcessor() {
		return noSQLData -> new MemberAllocationEntity(noSQLData.getId(),
				noSQLData.getCollectionName(), noSQLData.getViewName(), noSQLData.getFragmentId(), noSQLData.getMemberId());
	}

	@Bean
	public JpaItemWriter<MemberAllocationEntity> writer(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<MemberAllocationEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean
	public Job migrationJob(JobRepository jobRepository,
	                        PlatformTransactionManager transactionManager,
	                        MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fetch.MemberAllocationEntity> reader,
	                        JpaItemWriter<MemberAllocationEntity> writer) {
		Step step = new StepBuilder("memberAllocationMigrationStep", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.fetch.MemberAllocationEntity, MemberAllocationEntity>chunk(1000, transactionManager)
				.reader(reader)
				.writer(writer)
				.build();

		return new JobBuilder("memberAllocationMigrationJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.flow(step)
				.end()
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(FetchMigrationConfig.class, args);
	}
}
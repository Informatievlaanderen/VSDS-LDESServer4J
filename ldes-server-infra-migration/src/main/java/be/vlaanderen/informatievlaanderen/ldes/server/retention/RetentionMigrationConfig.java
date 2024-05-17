package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
public class RetentionMigrationConfig {

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity> memberPropertiesEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity, MemberPropertiesEntity> memberPropertiesEntityProcessor(MemberEntityMapper mapper) {
		return noSQLData -> new MemberPropertiesEntity(noSQLData.getId(), noSQLData.getCollectionName(),
				noSQLData.getViews(), noSQLData.isInEventSource(), noSQLData.getVersionOf(), noSQLData.getTimestamp());
	}

	@Bean
	public JpaItemWriter<MemberPropertiesEntity> memberPropertiesEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<MemberPropertiesEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationMongoRetention")
	public Job retentionMigrationJob(JobRepository jobRepository,
	                                    PlatformTransactionManager transactionManager,
	                                    MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity> reader,
	                                    ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity, MemberPropertiesEntity> processor,
	                                    JpaItemWriter<MemberPropertiesEntity> writer) {
		Step step = new StepBuilder("migrationMemberPropertiesEntity", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity, MemberPropertiesEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();

		return new JobBuilder("migrationMongoRetention", jobRepository)
				.incrementer(new RunIdIncrementer())
				.flow(step)
				.end()
				.build();
	}
}

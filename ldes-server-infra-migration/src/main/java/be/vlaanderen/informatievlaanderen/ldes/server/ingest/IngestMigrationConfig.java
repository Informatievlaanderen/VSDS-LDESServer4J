package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.mapper.MemberEntityMapper;
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
public class IngestMigrationConfig {

	public final MemberEntityMapper mapper = new MemberEntityMapper();

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity> memberEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity, MemberEntity> memberEntityProcessor() {
		return noSQLData -> mapper.toMemberEntity(noSQLData.toMember());
	}

	@Bean
	public JpaItemWriter<MemberEntity> memberEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<MemberEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationMongoIngest")
	public Job memberEntityMigrationJob(JobRepository jobRepository,
	                        PlatformTransactionManager transactionManager,
	                        MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity> reader,
	                        ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity, MemberEntity> processor,
	                        JpaItemWriter<MemberEntity> writer) {
		Step step = new StepBuilder("migrationMemberEntity", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity, MemberEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();

		return new JobBuilder("migrationMongoIngest", jobRepository)
				.incrementer(new RunIdIncrementer())
				.flow(step)
				.end()
				.build();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.fetch;


import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity.MemberAllocationEntity;
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
public class FetchMigrationConfig {
	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity> memberAllocationReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setCollection(be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity.FETCH_ALLOCATION);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity, MemberAllocationEntity> memberAllocationEntityProcessor() {
		return noSQLData -> new MemberAllocationEntity(noSQLData.getId(),
				noSQLData.getCollectionName(), noSQLData.getViewName(), noSQLData.getFragmentId(), noSQLData.getMemberId());
	}

	@Bean
	public JpaItemWriter<MemberAllocationEntity> memberAllocationEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<MemberAllocationEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationMongoFetch")
	public Job migrationMongoFetchJob(JobRepository jobRepository,
	                        PlatformTransactionManager transactionManager,
	                        MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity> reader,
	                        ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity, MemberAllocationEntity> processor,
	                        JpaItemWriter<MemberAllocationEntity> writer) {
		Step step = new StepBuilder("migrationMemberAllocation", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity, MemberAllocationEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();

		return new JobBuilder("migrationMongoFetch", jobRepository)
				.incrementer(new RunIdIncrementer())
				.flow(step)
				.end()
				.build();
	}
}
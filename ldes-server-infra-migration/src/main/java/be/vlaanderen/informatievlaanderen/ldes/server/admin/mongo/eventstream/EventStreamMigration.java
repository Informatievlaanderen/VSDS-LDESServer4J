package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.service.EventStreamConverter;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
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
public class EventStreamMigration {

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity> eventStreamEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity, EventStreamEntity> eventStreamEntityProcessor(EventStreamConverter mapper) {
		return noSQLData -> mapper.fromEventStream(noSQLData.toEventStream());
	}

	@Bean
	public JpaItemWriter<EventStreamEntity> eventStreamEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<EventStreamEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationEventStream")
	public Step migrationEventStream(JobRepository jobRepository,
	                                 PlatformTransactionManager transactionManager,
	                                 MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity> reader,
	                                 ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity, EventStreamEntity> processor,
	                                 JpaItemWriter<EventStreamEntity> writer) {
		return new StepBuilder("migrationEventStream", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity, EventStreamEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}

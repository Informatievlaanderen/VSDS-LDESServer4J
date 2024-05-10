package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.service.ViewEntityConverter;
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
public class ViewMigration {

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity> viewEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity, ViewEntity> viewEntityProcessor(ViewEntityConverter mapper) {
		return noSQLData -> mapper.fromView(noSQLData.toView());
	}

	@Bean
	public JpaItemWriter<ViewEntity> viewEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<ViewEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationView")
	public Step migrationView(JobRepository jobRepository,
	                                PlatformTransactionManager transactionManager,
	                                MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity> reader,
	                                ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity, ViewEntity> processor,
	                                JpaItemWriter<ViewEntity> writer) {
		return new StepBuilder("migrationView", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity, ViewEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.service.DcatServiceEntityConverter;
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
public class DataServiceMigration {

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity> dataServiceEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity, DataServiceEntity> dataServiceEntityProcessor(DcatServiceEntityConverter mapper) {
		return noSQLData -> mapper.fromDcatView(noSQLData.toDcatView());
	}

	@Bean
	public JpaItemWriter<DataServiceEntity> dataServiceEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<DataServiceEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationDataService")
	public Step migrationDataService(JobRepository jobRepository,
	                          PlatformTransactionManager transactionManager,
	                          MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity> reader,
	                          ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity, DataServiceEntity> processor,
	                          JpaItemWriter<DataServiceEntity> writer) {
		return new StepBuilder("migrationDataService", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity, DataServiceEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}

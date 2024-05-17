package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.entity.DcatCatalogEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatserver.service.DcatCatalogEntityConverter;
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
public class DcatCatalogMigration {
	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity> dcatCatalogEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity, DcatCatalogEntity> dcatCatalogEntityProcessor(DcatCatalogEntityConverter mapper) {
		return noSQLData -> mapper.fromDcatServer(noSQLData.toDcatServer());
	}

	@Bean
	public JpaItemWriter<DcatCatalogEntity> dcatCatalogEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<DcatCatalogEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationDcatCatalog")
	public Step migrationDcatCatalog(JobRepository jobRepository,
	                                   PlatformTransactionManager transactionManager,
	                                   MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity> reader,
	                                   ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity, DcatCatalogEntity> processor,
	                                   JpaItemWriter<DcatCatalogEntity> writer) {
		return new StepBuilder("migrationDcatCatalog", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatserver.entity.DcatCatalogEntity, DcatCatalogEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}

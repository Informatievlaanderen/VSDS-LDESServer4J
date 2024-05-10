package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.service.ShaclShapeEntityConverter;
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
public class ShaclShapeMigration {

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity> shaclShapeEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity, ShaclShapeEntity> shaclShapeEntityProcessor(ShaclShapeEntityConverter mapper) {
		return noSQLData -> mapper.fromShaclShape(noSQLData.toShaclShape());
	}

	@Bean
	public JpaItemWriter<ShaclShapeEntity> shaclShapeEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<ShaclShapeEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationShaclShape")
	public Step migrationShaclShape(JobRepository jobRepository,
	                                 PlatformTransactionManager transactionManager,
	                                 MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity> reader,
	                                 ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity, ShaclShapeEntity> processor,
	                                 JpaItemWriter<ShaclShapeEntity> writer) {
		return new StepBuilder("migrationShaclShape", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity.ShaclShapeEntity, ShaclShapeEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}

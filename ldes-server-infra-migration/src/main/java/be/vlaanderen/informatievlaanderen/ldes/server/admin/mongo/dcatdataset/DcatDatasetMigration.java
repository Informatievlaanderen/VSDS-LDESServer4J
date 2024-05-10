package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.service.DcatDatasetEntityConverter;
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
public class DcatDatasetMigration {
	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity> dcatDatasetEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity, DcatDatasetEntity> dcatDatasetEntityProcessor(DcatDatasetEntityConverter mapper) {
		return noSQLData -> mapper.datasetToEntity(noSQLData.toDcatDataset());
	}

	@Bean
	public JpaItemWriter<DcatDatasetEntity> dcatDatasetEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<DcatDatasetEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationDcatDataset")
	public Step migrationDcatDataset(JobRepository jobRepository,
	                                  PlatformTransactionManager transactionManager,
	                                  MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity> reader,
	                                  ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity, DcatDatasetEntity> processor,
	                                  JpaItemWriter<DcatDatasetEntity> writer) {
		return new StepBuilder("migrationDcatDataset", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity, DcatDatasetEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
}

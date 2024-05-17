package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.FragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.SequenceEntity;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(name = "ldes-server.migrate-mongo", havingValue = "true")
public class FragmentationMigrationConfig {

	// Fragment Entity
	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity> fragmentEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity, FragmentEntity> fragmentEntityProcessor() {
		return noSQLData -> FragmentEntity.fromLdesFragment(noSQLData.toLdesFragment());
	}

	@Bean
	public JpaItemWriter<FragmentEntity> fragmentEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<FragmentEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationFragmentEntity")
	public Step migrationFragmentEntityStep(JobRepository jobRepository,
	                                        PlatformTransactionManager transactionManager,
	                                        MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity> reader,
	                                        ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity, FragmentEntity> processor,
	                                        JpaItemWriter<FragmentEntity> writer) {
		return new StepBuilder("migrationFragmentEntity", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.FragmentEntity, FragmentEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	// Sequence Entity

	@Bean
	public MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity> sequenceEntityReader(MongoTemplate template) {
		MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity> reader = new MongoItemReader<>();
		reader.setTemplate(template);
		reader.setTargetType(be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity.class);
		reader.setQuery(new Query());
		return reader;
	}

	@Bean
	public ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity, SequenceEntity> sequenceEntityProcessor() {
		return noSQLData -> new SequenceEntity(noSQLData.getViewName(), noSQLData.getLastProcessedSequence());
	}

	@Bean
	public JpaItemWriter<SequenceEntity> sequenceEntityWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<SequenceEntity> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	@Bean("migrationSequenceEntity")
	public Step migrationSequenceEntityStep(JobRepository jobRepository,
	                                        PlatformTransactionManager transactionManager,
	                                        MongoItemReader<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity> reader,
	                                        ItemProcessor<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity, SequenceEntity> processor,
	                                        JpaItemWriter<SequenceEntity> writer) {
		return new StepBuilder("migrationFragmentEntity", jobRepository)
				.<be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity.SequenceEntity, SequenceEntity>chunk(1000, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}

	// Job

	@Bean("migrationMongoFragmentation")
	public Job memberEntityMigrationJob(JobRepository jobRepository,
	                                    @Qualifier("migrationFragmentEntity") Step fragmentEntityStep,
                                        @Qualifier("migrationSequenceEntity") Step sequenceEntityStep
	                                    ) {

		return new JobBuilder("migrationMongoFragmentation", jobRepository)
				.incrementer(new RunIdIncrementer())
				.flow(fragmentEntityStep)
				.next(sequenceEntityStep)
				.end()
				.build();
	}


}

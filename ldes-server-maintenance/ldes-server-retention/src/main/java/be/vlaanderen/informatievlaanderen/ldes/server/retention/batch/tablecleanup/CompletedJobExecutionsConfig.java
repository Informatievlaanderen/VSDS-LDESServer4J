package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.tablecleanup;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.springframework.batch.item.database.Order.ASCENDING;

@Configuration
public class CompletedJobExecutionsConfig {

	@Bean
	public Step completedJobExecutionsStep(JobRepository jobRepository,
	                                       PlatformTransactionManager platformTransactionManager,
	                                       ItemReader<CompletedJobExecution> completedJobExecutionsReader,
	                                       ItemWriter<CompletedJobExecution> batchTableDeleter) {
		return new StepBuilder("completedJobExecutionsStep", jobRepository)
				.<CompletedJobExecution, CompletedJobExecution>chunk(100, platformTransactionManager)
				.reader(completedJobExecutionsReader)
				.writer(batchTableDeleter)
				.build();
	}

	@Bean
	public ItemReader<CompletedJobExecution> completedJobExecutionsReader(DataSource dataSource) {
		return new JdbcPagingItemReaderBuilder<CompletedJobExecution>()
				.name("completedJobExecutionsReader")
				.selectClause("JOB_EXECUTION_ID, JOB_INSTANCE_ID")
				.fromClause("BATCH_JOB_EXECUTION")
				.whereClause("EXIT_CODE = 'COMPLETED'")
				.sortKeys(Map.of("JOB_EXECUTION_ID", ASCENDING))
				.pageSize(250)
				.maxItemCount(1000)
				.dataSource(dataSource)
				.rowMapper((rs, rowNum) -> new CompletedJobExecution(rs.getLong("JOB_EXECUTION_ID"), rs.getLong("JOB_INSTANCE_ID")))
				.build();
	}

	@Bean
	public ItemWriter<CompletedJobExecution> batchTableDeleter(List<ItemWriter<? super CompletedJobExecution>> delegates) {
		return new CompositeItemWriter<>(delegates);
	}

	@Bean
	@Order(1)
	public ItemWriter<CompletedJobExecution> batchStepExecutionContextDeleter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CompletedJobExecution>()
				.dataSource(dataSource)
				.sql("DELETE FROM BATCH_STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN (SELECT STEP_EXECUTION_ID FROM BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID = ?)")
				.itemPreparedStatementSetter((jobExecution, ps) -> ps.setLong(1, jobExecution.jobExecutionId()))
				.build();
	}

	@Bean
	@Order(2)
	public ItemWriter<CompletedJobExecution> batchStepExecutionDeleter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CompletedJobExecution>()
				.dataSource(dataSource)
				.sql("DELETE FROM BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID = ?")
				.itemPreparedStatementSetter((jobExecution, ps) -> ps.setLong(1, jobExecution.jobExecutionId()))
				.build();
	}

	@Bean
	@Order(3)
	public ItemWriter<CompletedJobExecution> batchJobExecutionContextDeleter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CompletedJobExecution>()
				.dataSource(dataSource)
				.sql("DELETE FROM BATCH_JOB_EXECUTION_CONTEXT WHERE JOB_EXECUTION_ID = ?")
				.itemPreparedStatementSetter((jobExecution, ps) -> ps.setLong(1, jobExecution.jobExecutionId()))
				.build();
	}

	@Bean
	@Order(4)
	public ItemWriter<CompletedJobExecution> batchJobExecutionParamsDeleter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CompletedJobExecution>()
				.dataSource(dataSource)
				.sql("DELETE FROM BATCH_JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID = ?")
				.itemPreparedStatementSetter((jobExecution, ps) -> ps.setLong(1, jobExecution.jobExecutionId()))
				.build();
	}

	@Bean
	@Order(5)
	public ItemWriter<CompletedJobExecution> batchJobExecutionDeleter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CompletedJobExecution>()
				.dataSource(dataSource)
				.sql("DELETE FROM BATCH_JOB_EXECUTION WHERE JOB_EXECUTION_ID = ?")
				.itemPreparedStatementSetter((jobExecution, ps) -> ps.setLong(1, jobExecution.jobExecutionId()))
				.build();
	}

	@Bean
	@Order(6)
	public ItemWriter<CompletedJobExecution> batchJobInstanceDeleter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CompletedJobExecution>()
				.dataSource(dataSource)
				.sql("DELETE FROM BATCH_JOB_INSTANCE WHERE JOB_INSTANCE_ID = ?")
				.itemPreparedStatementSetter((jobExecution, ps) -> ps.setLong(1, jobExecution.jobInstanceId()))
				.build();
	}

}

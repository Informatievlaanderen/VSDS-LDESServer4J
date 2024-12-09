package be.vlaanderen.informatievlaanderen.ldes.server.retention.batch.tablecleanup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompletedJobExecutionsConfigTest {
	private ApplicationContextRunner applicationContextRunner;

	@BeforeEach
	void setUp() {
		applicationContextRunner = new ApplicationContextRunner()
				.withBean(JobRepository.class, Mockito::mock)
				.withBean(PlatformTransactionManager.class, Mockito::mock)
				.withBean(DataSource.class, MockedDataSource::new)
				.withUserConfiguration(CompletedJobExecutionsConfig.class);
	}

	@Test
	void test_PresenceOfReader() {
		applicationContextRunner.run(context ->
				assertThat(context).hasSingleBean(ItemReader.class)
		);
	}

	@Test
	void test_PresenceOfWriters() {
		applicationContextRunner.run(context -> {
			assertThat(context.getBeansOfType(ItemWriter.class)).hasSize(7);
			assertThat(context).hasSingleBean(CompositeItemWriter.class);
			assertThat(context.getBeansOfType(JdbcBatchItemWriter.class)).hasSize(6);
		});
	}

	@Test
	void test_PresenceOfStep() {
		applicationContextRunner.run(context ->
				assertThat(context).hasSingleBean(Step.class)
		);
	}

	static class MockedDataSource extends AbstractDataSource {
		@Override
		public Connection getConnection() throws SQLException {
			Connection connection = mock();
			when(connection.prepareStatement(anyString())).thenReturn(mock());
			DatabaseMetaData metaData = mock();
			when(connection.getMetaData()).thenReturn(metaData);
			when(metaData.getDatabaseProductName()).thenReturn("H2");
			return connection;
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			return getConnection();
		}
	}
}
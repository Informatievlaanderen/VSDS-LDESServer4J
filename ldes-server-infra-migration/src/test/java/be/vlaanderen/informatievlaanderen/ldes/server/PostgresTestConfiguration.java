package be.vlaanderen.informatievlaanderen.ldes.server;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.sql.DataSource;

@SpringBootConfiguration
public class PostgresTestConfiguration {
//	@Value("${spring.datasource.url}")
//	private String url;
//
//	@Value("${spring.datasource.username}")
//	private String username;
//
//	@Value("${spring.datasource.password}")
//	private String password;


	@Bean
	public PostgreSQLContainer postgresqlContainer() {
		var container = new PostgreSQLContainer("postgres:13.1")
				.withDatabaseName("test")
				.withUsername("postgres")
				.withPassword("postgres")
				.waitingFor(Wait.forListeningPort());

		container.start();
		return (PostgreSQLContainer) container;
	}

	@Bean
	@DependsOn("postgresqlContainer")
	public DataSource dataSource(PostgreSQLContainer container) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(container.getJdbcUrl());
		dataSource.setUsername(container.getUsername());
		dataSource.setPassword(container.getPassword());
		return dataSource;
	}

	@Bean
	@DependsOn("dataSource")
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		final JpaTransactionManager tm = new JpaTransactionManager();
		tm.setDataSource(dataSource);
		return tm;
	}

}

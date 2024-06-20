package be.vlaanderen.informatievlaanderen.ldes.server.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setChangeLog("classpath:/db/changelog/master.xml"); // Set your desired change log path
		liquibase.setDataSource(dataSource); // Configure your data source
		return liquibase;
	}
}

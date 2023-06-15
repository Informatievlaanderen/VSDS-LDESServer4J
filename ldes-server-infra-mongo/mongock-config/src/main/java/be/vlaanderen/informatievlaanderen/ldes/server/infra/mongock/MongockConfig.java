package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongock;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.springboot.EnableMongock;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.utils.CollectionUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
@EnableMongock
public class MongockConfig {

	@Bean
	public MongockRunner applicationRunner(MongoTemplate mongoTemplate,
			ApplicationContext applicationContext,
			MigrationScanPackages migrationScanPackages) {
		final List<String> migrationPackages = migrationScanPackages.getMigrationScanPackage();
		if (CollectionUtils.isNotNullOrEmpty(migrationPackages)) {
			return MongockSpringboot.builder()
					.setDriver(SpringDataMongoV4Driver.withDefaultLock(mongoTemplate))
					.addMigrationScanPackages(migrationPackages)
					.setSpringContext(applicationContext)
					.setEnabled(true)
					.buildRunner();
		} else {
			return new EmptyMongockRunner();
		}
	}

}

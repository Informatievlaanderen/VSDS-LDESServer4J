package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

public class Config {

	@Bean
	public MongockInitializingBeanRunner mongockRunner(ConnectionDriver driver, ApplicationContext applicationContext) {
		return MongockSpringboot.builder()
				.setDriver(driver)
				.setSpringContext(applicationContext)
				.buildInitializingBeanRunner();
	}
}
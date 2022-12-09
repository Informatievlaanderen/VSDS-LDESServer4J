package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {

	@Bean
	public ExecutorService executorService() {
		return Executors.newCachedThreadPool();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {

	public ExecutorService substringFragmentationStrategyWrapper() {
		return Executors.newCachedThreadPool();
	}
}

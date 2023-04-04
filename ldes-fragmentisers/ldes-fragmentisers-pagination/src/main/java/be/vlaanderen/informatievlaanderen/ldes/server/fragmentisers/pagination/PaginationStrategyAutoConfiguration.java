package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class PaginationStrategyAutoConfiguration {

	@Bean("pagination")
	public PaginationStrategyWrapper paginationStrategyWrapper() {
		return new PaginationStrategyWrapper();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.PaginationStrategy.PAGINATION_FRAGMENTATION;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class PaginationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(PAGINATION_FRAGMENTATION)
	public PaginationStrategyWrapper paginationStrategyWrapper() {
		return new PaginationStrategyWrapper();
	}
}

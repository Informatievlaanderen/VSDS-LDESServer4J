package be.vlaanderen.informatievlaanderen.ldes.server;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public GroupedOpenApi adminGroup() {
		String[] packages = { "be.vlaanderen.informatievlaanderen.ldes.server.admin.rest" };
		return GroupedOpenApi.builder().group("admin").packagesToScan(packages)
				.build();
	}

	@Bean
	public GroupedOpenApi defaultGroup() {
		String[] packages = { "be.vlaanderen.informatievlaanderen.ldes.server.admin.rest" };
		return GroupedOpenApi.builder().group("base").packagesToExclude(packages)
				.build();
	}
}

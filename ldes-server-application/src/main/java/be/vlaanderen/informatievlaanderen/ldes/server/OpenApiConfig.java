package be.vlaanderen.informatievlaanderen.ldes.server;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String VERSION = "3.1.0";

    @Bean
    public GroupedOpenApi adminGroup() {
        String[] packages = {"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest"};
        return GroupedOpenApi.builder()
                .group("admin")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("LDES Server Admin API")
                        .version(VERSION)
                        .description("This API makes it possible to manage an LDES Server")
                ))
                .packagesToScan(packages)
                .build();
    }

    @Bean
    public GroupedOpenApi defaultGroup() {
        String[] packages = {"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest"};
        return GroupedOpenApi.builder()
                .group("base")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Ingest and Fetch endpoints")
                        .version(VERSION)
                        .description("These API endpoints are available to ingest and fetch linked data")
                ))
                .packagesToExclude(packages)
                .build();
    }
}

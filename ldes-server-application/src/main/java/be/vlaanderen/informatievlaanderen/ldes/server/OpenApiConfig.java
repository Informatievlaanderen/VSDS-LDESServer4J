package be.vlaanderen.informatievlaanderen.ldes.server;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi adminGroup(BuildProperties buildProperties) {
        String[] packages = {"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest"};
        return GroupedOpenApi.builder()
                .group("admin")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("LDES Server Admin API")
                        .version(buildProperties.getVersion())
                        .description("This API makes it possible to manage an LDES Server")
                ))
                .packagesToScan(packages)
                .build();
    }

    @Bean
    public GroupedOpenApi kafkaDebugGroup(BuildProperties buildProperties) {
        String[] packages = {"be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka"};
        return GroupedOpenApi.builder()
                .group("kafka-debug")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("LDES Server Kafka Debug API")
                        .version(buildProperties.getVersion())
                        .description("Purely meant as a developer debugging tool")
                ))
                .packagesToScan(packages)
                .build();
    }

    @Bean
    public GroupedOpenApi defaultGroup(BuildProperties buildProperties) {
        String[] packages = {"be.vlaanderen.informatievlaanderen.ldes.server.admin.rest"};
        return GroupedOpenApi.builder()
                .group("base")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Ingest and Fetch endpoints")
                        .version(buildProperties.getVersion())
                        .description("These API endpoints are available to ingest and fetch linked data")
                ))
                .packagesToExclude(packages)
                .build();
    }
}

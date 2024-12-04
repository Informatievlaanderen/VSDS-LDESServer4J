package be.vlaanderen.informatievlaanderen.ldes.server;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String ADMIN_PACKAGE = "be.vlaanderen.informatievlaanderen.ldes.server.admin.rest";
    private static final String KAFKA_DEBUG_PACKAGE = "be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka";
    @Bean
    public GroupedOpenApi adminGroup(BuildProperties buildProperties) {
        return GroupedOpenApi.builder()
                .group("admin")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("LDES Server Admin API")
                        .version(buildProperties.getVersion())
                        .description("This API makes it possible to manage an LDES Server")
                ))
                .packagesToScan(ADMIN_PACKAGE)
                .build();
    }

    @Bean
    public GroupedOpenApi kafkaDebugGroup(BuildProperties buildProperties) {
        return GroupedOpenApi.builder()
                .group("kafka-debug")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("LDES Server Kafka Debug API")
                        .version(buildProperties.getVersion())
                        .description("Purely meant as a developer debugging tool")
                ))
                .packagesToScan(KAFKA_DEBUG_PACKAGE)
                .build();
    }

    @Bean
    public GroupedOpenApi defaultGroup(BuildProperties buildProperties) {
        return GroupedOpenApi.builder()
                .group("base")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Ingest and Fetch endpoints")
                        .version(buildProperties.getVersion())
                        .description("These API endpoints are available to ingest and fetch linked data")
                ))
                .packagesToExclude(ADMIN_PACKAGE, KAFKA_DEBUG_PACKAGE)
                .build();
    }
}

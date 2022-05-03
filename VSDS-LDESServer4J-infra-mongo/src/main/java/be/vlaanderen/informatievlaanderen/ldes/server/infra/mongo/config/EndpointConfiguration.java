package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties
@Getter
@Setter
@ConfigurationProperties(prefix = "ldes")
public class EndpointConfiguration {
    private String endpoint;
}

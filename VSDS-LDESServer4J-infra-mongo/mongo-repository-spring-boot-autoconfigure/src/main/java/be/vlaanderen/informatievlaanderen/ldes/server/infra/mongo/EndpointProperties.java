package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ldes")
@Getter
@Setter
public class EndpointProperties {

    private String endpoint;
}

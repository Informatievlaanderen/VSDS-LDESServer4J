package be.vlaanderen.informatievlaanderen.ldes.server.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ldes")
public class LdesConfig {
    private String id;
    private String context;

    public String getId() {
        return id;
    }

    public String getContext() {
        return context;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContext(String context) {
        this.context = context;
    }
}

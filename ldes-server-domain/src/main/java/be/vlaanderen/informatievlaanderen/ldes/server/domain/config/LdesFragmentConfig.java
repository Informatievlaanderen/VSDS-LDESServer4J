package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ldes")
public class LdesFragmentConfig {
    private String view;
    private String context;
    private String shape;

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public Map<String, String> toMap() {
        return Map.of("view", view, "context", context, "shape", shape);
    }
}

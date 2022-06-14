package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ldes")
public class LdesFragmentConfig {
    private String view;
    private String shape;

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public Map<String, String> toMap() {
        Map<String, String> configMap = new HashMap<>();
        ofNullable(getView()).ifPresentOrElse(view -> configMap.put("view", view), () -> {
            throw new RuntimeException("Fragment configuration: missing view");
        });
        ofNullable(getShape()).ifPresent(shape -> configMap.put("shape", shape));
        return configMap;
    }
}

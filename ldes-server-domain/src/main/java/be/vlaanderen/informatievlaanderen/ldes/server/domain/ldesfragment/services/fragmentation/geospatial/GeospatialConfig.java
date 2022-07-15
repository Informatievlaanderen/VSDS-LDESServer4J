package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.fragmentation.geospatial;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "geospatial")
public class GeospatialConfig {

    private Long zoomLevel;

    public Long getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(Long zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
}

package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "geospatial")
public class GeospatialConfig {

    private String bucketiserProperty;
    private int zoomLevel;
    
    public String getBucketiserProperty() {
    	return bucketiserProperty;
    }
    
    public void setBucketiserProperty(String bucketiserProperty) {
    	this.bucketiserProperty = bucketiserProperty;
    }
    
    public int getZoomLevel() {
    	return zoomLevel;
    }
    
    public void setZoomLevel(int zoomLevel) {
    	this.zoomLevel = zoomLevel;
    }
}

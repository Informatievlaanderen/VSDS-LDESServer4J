package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "geospatial-hierarchy")
public class HierarchyGeospatialConfig {

	private String bucketiserProperty;
	private int maxZoomLevel;

	public String getBucketiserProperty() {
		return bucketiserProperty;
	}

	public void setBucketiserProperty(String bucketiserProperty) {
		this.bucketiserProperty = bucketiserProperty;
	}

	public int getMaxZoomLevel() {
		return maxZoomLevel;
	}

	public void setMaxZoomLevel(int zoomLevel) {
		this.maxZoomLevel = zoomLevel;
	}
}

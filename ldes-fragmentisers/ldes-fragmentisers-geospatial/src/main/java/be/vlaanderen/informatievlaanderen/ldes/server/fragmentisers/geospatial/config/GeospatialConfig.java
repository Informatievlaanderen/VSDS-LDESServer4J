package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config;

public class GeospatialConfig {

	private String bucketiserProperty;
	private Integer maxZoomLevel;

	private String projection;

	public String getBucketiserProperty() {
		return bucketiserProperty;
	}

	public void setBucketiserProperty(String bucketiserProperty) {
		this.bucketiserProperty = bucketiserProperty;
	}

	public int getMaxZoomLevel() {
		return maxZoomLevel;
	}

	public void setMaxZoomLevel(Integer zoomLevel) {
		this.maxZoomLevel = zoomLevel;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config;

public class GeospatialConfig {
	private String fragmenterSubjectFilter = ".*";
	private String fragmenterProperty;
	private Integer maxZoomLevel;
	private String projection;

	public String getFragmenterSubjectFilter() {
		return fragmenterSubjectFilter;
	}

	public void setFragmenterSubjectFilter(String fragmenterSubjectFilter) {
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
	}

	public String getFragmenterProperty() {
		return fragmenterProperty;
	}

	public void setFragmenterProperty(String fragmenterProperty) {
		this.fragmenterProperty = fragmenterProperty;
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

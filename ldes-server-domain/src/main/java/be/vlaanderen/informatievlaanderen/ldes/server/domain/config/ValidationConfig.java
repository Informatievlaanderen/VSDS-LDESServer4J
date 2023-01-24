package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

public class ValidationConfig {
	private String shape;
	private Boolean enabled = false;

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.List;

public class LdesConfig {

	private String hostName;
	private String collectionName;
	private String memberType;
	private String timestampPath;
	private String versionOf;
	private Validation validation = new Validation();
	private Model dcat = ModelFactory.createDefaultModel();
	private ViewConfig viewConfig = ViewConfig.empty();

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public void setTimestampPath(String timestampPath) {
		this.timestampPath = timestampPath;
	}

	public String getVersionOfPath() {
		return versionOf;
	}

	public void setVersionOf(String versionOf) {
		this.versionOf = versionOf;
	}

	public Validation validation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

	public static class Validation {

		private String shape;
		private boolean enabled = true;

		public String getShape() {
			return shape;
		}

		public void setShape(String shape) {
			this.shape = shape;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

	public List<ViewSpecification> getViews() {
		return viewConfig.getViews(collectionName);
	}

	public void setViews(List<ViewSpecification> views) {
		views.forEach(viewSpec -> viewSpec.setCollectionName(collectionName));
		viewConfig = viewConfig.withViews(views);
	}

	public void setDcat(Model dcat) {
		this.dcat = dcat;
	}

	public Model getDcat() {
		return dcat;
	}

	public void setDefaultView(boolean defaultView) {
		viewConfig = viewConfig.withDefaultView(defaultView);
	}

	public String getBaseUrl() {
		return hostName + "/" + collectionName;
	}

}

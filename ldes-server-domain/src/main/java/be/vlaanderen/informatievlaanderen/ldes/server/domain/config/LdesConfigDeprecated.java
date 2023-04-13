package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Deprecated
@Configuration
@ConfigurationProperties(prefix = "ldes")
public class LdesConfigDeprecated {

	private String hostName;
	private String memberType;
	private String collectionName;
	private String timestampPath;
	private Validation validation = new Validation();
	private String versionOf;
	private Model dcat = ModelFactory.createDefaultModel();

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setTimestampPath(String timestampPath) {
		this.timestampPath = timestampPath;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public void setVersionOf(String versionOf) {
		this.versionOf = versionOf;
	}

	public String getVersionOfPath() {
		return versionOf;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

	public Validation validation() {
		return validation;
	}

	public String getBaseUrl() {
		return hostName + "/" + collectionName;
	}

	public static class Validation {
		private String shape;
		private boolean enabled = true;

		public void setShape(String shape) {
			this.shape = shape;
		}

		public String getShape() {
			return shape;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}
	}

	public Model getDcat() {
		return dcat;
	}

	public void setDcat(Model dcat) {
		this.dcat = dcat;
	}

}

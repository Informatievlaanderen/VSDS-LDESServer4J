package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldes")
public class LdesConfig {

	private String hostName;
	private String collectionName;
	private ValidationConfig validation;
	private String memberType;
	private String timestampPath;
	private String versionOf;

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

	public ValidationConfig validation() {
		return validation;
	}

	public void setValidation(ValidationConfig validation) {
		this.validation = validation;
	}
}

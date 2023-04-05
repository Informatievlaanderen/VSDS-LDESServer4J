package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldes")
public class LdesConfig {

	private String hostName;
	private String collectionName;
	private String shape;
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

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
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
}

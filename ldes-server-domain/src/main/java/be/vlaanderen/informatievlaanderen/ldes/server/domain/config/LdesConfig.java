package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "ldes")
public class LdesConfig {
	private String hostName;
	private String collectionName;
	private String shape;
	private String memberType;
	private String timestampPath;
	private String versionOf;
	private Map<String, Object> fragmentations;

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

	public String getVersionOf() {
		return versionOf;
	}

	public void setVersionOf(String versionOf) {
		this.versionOf = versionOf;
	}

	public Map<String, Object> getFragmentations() {
		return fragmentations;
	}

	public void setFragmentations(Map<String, Object> fragmentations) {
		this.fragmentations = fragmentations;
	}
}

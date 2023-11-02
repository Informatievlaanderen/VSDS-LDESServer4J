package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldes-server")
public class ServerConfig {
	private static final String DEFAULT_COMPACTION_DURATION = "P7D";
	private String hostName;
	private String compactionDuration;
	private Integer retentionInterval;

	public String getHostName() {
		return hostName;
	}

	public String getCompactionDuration() {
		return compactionDuration != null ? compactionDuration : DEFAULT_COMPACTION_DURATION;
	}

	public int getRetentionInterval() {
		if(retentionInterval == null) {
			retentionInterval = 10000;
		}
		return retentionInterval;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setCompactionDuration(String compactionDuration) {
		this.compactionDuration = compactionDuration;
	}

	public void setRetentionInterval(Integer retentionInterval) {
		this.retentionInterval = retentionInterval;
	}

	public static final String HOST_NAME_KEY = "${ldes-server.host-name}";
	public static final String RETENTION_INTERVAL_KEY = "${ldes-server.retention-interval:10000}";

}

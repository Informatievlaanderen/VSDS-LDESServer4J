package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldes-server")
public class ServerConfig {
	private static final String DEFAULT_COMPACTION_DURATION = "P7D";
	private static final String DEFAULT_RETENTION_CRON = "*/10 * * * * *";
	public static final String SWAGGER_UI_PATH_KEY = "${springdoc.swagger-ui.path}";
	public static final String HOST_NAME_KEY = "${ldes-server.host-name}";
	public static final String RETENTION_CRON_KEY = "${ldes-server.retention-cron: " + DEFAULT_RETENTION_CRON + "}";
	private String hostName;
	private String compactionDuration;
	private String retentionCron;

	public String getHostName() {
		return hostName;
	}

	public String getCompactionDuration() {
		return compactionDuration != null ? compactionDuration : DEFAULT_COMPACTION_DURATION;
	}

	public String getRetentionCron() {
		return retentionCron != null ? retentionCron : DEFAULT_RETENTION_CRON;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setCompactionDuration(String compactionDuration) {
		this.compactionDuration = compactionDuration;
	}

	public void setRetentionCron(String retentionCron) {
		this.retentionCron = retentionCron;
	}

}

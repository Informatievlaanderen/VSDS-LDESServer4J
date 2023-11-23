package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldes-server")
public class ServerConfig {
	private static final String DEFAULT_COMPACTION_DURATION = "P7D";
	private static final String DEFAULT_BACKGROUND_CRON = "0 0 0 * * *";
	private static final Boolean DEFAULT_USE_RELATIVE_URL = Boolean.FALSE;
	public static final String HOST_NAME_KEY = "${ldes-server.host-name}";
	public static final String RETENTION_CRON_KEY = "${ldes-server.retention-cron: " + DEFAULT_BACKGROUND_CRON + "}";
	public static final String DELETION_CRON_KEY = "${ldes-server.deletion-cron:" + DEFAULT_BACKGROUND_CRON + "}";
	public static final String COMPACTION_CRON_KEY = "${ldes-server.compaction-cron:" + DEFAULT_BACKGROUND_CRON + "}";
	public static final Boolean USE_RELATIVE_URL_KEY = Boolean.valueOf("${ldes-server.use-relative-url:" + DEFAULT_USE_RELATIVE_URL + "}");
	private String hostName;
	private String compactionDuration;
	private String retentionCron;
	private String deletionCron;
	private String compactionCron;
	private Boolean useRelativeUrl;

	public String getHostName() {
		return hostName;
	}

	public String getCompactionDuration() {
		return compactionDuration != null ? compactionDuration : DEFAULT_COMPACTION_DURATION;
	}

	public String getRetentionCron() {
		return retentionCron != null ? retentionCron : DEFAULT_BACKGROUND_CRON;
	}

	public String getDeletionCron() {
		return deletionCron != null ? deletionCron : DEFAULT_BACKGROUND_CRON;
	}

	public String getCompactionCron() {
		return compactionCron != null ? compactionCron : DEFAULT_BACKGROUND_CRON;
	}
	public Boolean getUseRelativeUrl() {
		return useRelativeUrl != null ? useRelativeUrl : DEFAULT_USE_RELATIVE_URL;
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

	public void setDeletionCron(String deletionCron) {
		this.deletionCron = deletionCron;
	}

	public void setCompactionCron(String compactionCron) {
		this.compactionCron = compactionCron;
	}

	public void setUseRelativeUrl(Boolean useRelativeUrl) {
		this.useRelativeUrl = useRelativeUrl;
	}
}

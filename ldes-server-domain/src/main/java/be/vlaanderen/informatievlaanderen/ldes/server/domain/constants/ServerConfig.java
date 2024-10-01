package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ldes-server")
public class ServerConfig {
	private static final String DEFAULT_COMPACTION_DURATION = "P7D";
	private static final String DEFAULT_BACKGROUND_CRON = "0 0 0 * * *";
	private static final String DEFAULT_FRAGMENTATION_CRON = "*/30 * * * * *";
	private static final String DEFAULT_USE_RELATIVE_URL = "false";
	private static final String DEFAULT_MAX_JSONLD_CACHE_CAPACITY = "100";
	public static final String HOST_NAME_KEY = "${ldes-server.host-name}";
	public static final String MAINTENANCE_CRON_KEY = "${ldes-server.maintenance-cron: " + DEFAULT_BACKGROUND_CRON + "}";
	public static final String FRAGMENTATION_CRON = "${ldes-server.fragmentation-cron:" + DEFAULT_FRAGMENTATION_CRON + "}";
	public static final String USE_RELATIVE_URL_KEY = "${ldes-server.use-relative-url:" + DEFAULT_USE_RELATIVE_URL + "}";
	public static final String MAX_JSONLD_CACHE_CAPACITY = "${ldes-server.max-jsonld-cache-capacity:" + DEFAULT_MAX_JSONLD_CACHE_CAPACITY + "}";


	private String hostName;
	private String compactionDuration;
	private String maintenanceCron;
	private String fragmentationCron;
	private Boolean useRelativeUrl;
	private Integer maxJsonldCacheCapacity;

	public String getHostName() {
		return hostName;
	}

	public String getCompactionDuration() {
		return compactionDuration != null ? compactionDuration : DEFAULT_COMPACTION_DURATION;
	}

	public String getMaintenanceCron() {
		return maintenanceCron != null ? maintenanceCron : DEFAULT_BACKGROUND_CRON;
	}

	public String getFragmentationCron() {
		return fragmentationCron != null ? fragmentationCron : DEFAULT_FRAGMENTATION_CRON;
	}

	public Boolean getUseRelativeUrl() {
		return useRelativeUrl != null ? useRelativeUrl : Boolean.getBoolean(DEFAULT_USE_RELATIVE_URL);
	}

	public int getMaxJsonldCacheCapacity() {
		return maxJsonldCacheCapacity != null ? maxJsonldCacheCapacity : Integer.valueOf(DEFAULT_MAX_JSONLD_CACHE_CAPACITY);
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setCompactionDuration(String compactionDuration) {
		this.compactionDuration = compactionDuration;
	}

	public void setMaintenanceCron(String maintenanceCron) {
		this.maintenanceCron = maintenanceCron;
	}

	public void setFragmentationCron(String fragmentationCron) {
		this.fragmentationCron = fragmentationCron;
	}

	public void setUseRelativeUrl(Boolean useRelativeUrl) {
		this.useRelativeUrl = useRelativeUrl;
	}

	public void setMaxJsonldCacheCapacity(int maxJsonldCacheCapacity) {
		this.maxJsonldCacheCapacity = maxJsonldCacheCapacity;
	}

}

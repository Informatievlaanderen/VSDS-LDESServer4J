package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "timebased")
public class SequentialFragmentationConfig {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private Long memberLimit;

	public Long getMemberLimit() {
		return memberLimit;
	}

	public void setMemberLimit(Long memberLimit) {
		this.memberLimit = memberLimit;
	}

	public DateTimeFormatter getDatetimeFormatter() {
		return formatter;
	}
}

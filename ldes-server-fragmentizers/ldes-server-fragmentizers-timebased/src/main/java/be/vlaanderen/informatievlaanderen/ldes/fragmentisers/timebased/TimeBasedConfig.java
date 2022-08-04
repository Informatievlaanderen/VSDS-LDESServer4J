package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "timebased")
public class TimeBasedConfig {

    private Long memberLimit;

    public Long getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Long memberLimit) {
        this.memberLimit = memberLimit;
    }
}

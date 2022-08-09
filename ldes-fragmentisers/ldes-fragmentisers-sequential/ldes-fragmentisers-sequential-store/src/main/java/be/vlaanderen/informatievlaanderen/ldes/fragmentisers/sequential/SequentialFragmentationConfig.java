package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "timebased")
public class SequentialFragmentationConfig {

    private Long memberLimit;

    public Long getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Long memberLimit) {
        this.memberLimit = memberLimit;
    }
}

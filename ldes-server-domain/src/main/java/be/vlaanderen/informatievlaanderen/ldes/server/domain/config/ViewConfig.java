package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "view")
public class ViewConfig {
    private String shape;
    private String timestampPath;
    private String versionOfPath;
    private Long memberLimit;

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getTimestampPath() {
        return timestampPath;
    }

    public void setTimestampPath(String timestampPath) {
        this.timestampPath = timestampPath;
    }

    public String getVersionOfPath() {
        return versionOfPath;
    }

    public void setVersionOfPath(String versionOfPath) {
        this.versionOfPath = versionOfPath;
    }

    public Long getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Long memberLimit) {
        this.memberLimit = memberLimit;
    }
}

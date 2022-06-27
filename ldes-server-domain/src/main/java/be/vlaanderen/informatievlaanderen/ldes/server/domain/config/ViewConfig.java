package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String getTimestampPathValue() {
        Pattern pattern = Pattern.compile("([^^]*)\\^\\^(.*)");
        Matcher matcher = pattern.matcher(timestampPath);
        if(matcher.find()){
            return matcher.group(1);
        }
        else {
            return timestampPath;
        }
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

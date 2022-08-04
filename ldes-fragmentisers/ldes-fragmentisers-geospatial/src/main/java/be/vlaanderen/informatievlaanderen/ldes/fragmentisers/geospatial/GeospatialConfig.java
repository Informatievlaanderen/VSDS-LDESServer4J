package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "geospatial")
public class GeospatialConfig {

    private Long memberLimit;
    private String bucketiserProperty;
    private int maxZoomLevel;

    public Long getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Long memberLimit) {
        this.memberLimit = memberLimit;
    }
    
    public String getBucketiserProperty() {
    	return bucketiserProperty;
    }
    
    public void setBucketiserProperty(String bucketiserProperty) {
    	this.bucketiserProperty = bucketiserProperty;
    }
    
    public int getMaxZoomLevel() {
    	return maxZoomLevel;
    }
    
    public void setMaxZoomLevel(int maxZoomLevel) {
    	this.maxZoomLevel = maxZoomLevel;
    }
}

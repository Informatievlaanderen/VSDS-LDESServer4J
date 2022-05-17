package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ldes")
public class EndpointProperties {

    private String endpoint;
    
    public void setEndpoint(String endpoint) {
    	this.endpoint = endpoint;
    }
    
    public String getEndpoint() {
    	return this.endpoint;
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config;

import java.util.Properties;

public class EndpointConfig extends Properties {
	
    /** Implements Serializable. */
	private static final long serialVersionUID = 1L;
	
	private final String endpoint;
    
    public EndpointConfig(final String endpoint) {
    	this.endpoint = endpoint;
    }
    
    public String getEndpoint() {
    	return this.endpoint;
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesfragment")
public class LdesFragmentEntity {

    @Id
    private final Integer id;

    private final JSONObject ldesFragment;
    
    public LdesFragmentEntity(final Integer id, final JSONObject ldesFragment) {
    	this.id = id;
    	this.ldesFragment = ldesFragment;
    }
    
    public Integer getId() {
    	return this.id;
    }
    
    public JSONObject getLdesFragment() {
    	return this.ldesFragment;
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesmember")
public class LdesMemberEntity {

    @Id
    private final Integer id;

    private final JSONObject ldesMember;
    
    public LdesMemberEntity(final Integer id, final JSONObject ldesMember) {
    	this.id = id;
    	this.ldesMember = ldesMember;
    }
    
    public Integer getId() {
    	return this.id;
    }
    
    public JSONObject getLdesMember() {
    	return this.ldesMember;
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoMapKeyDotReplacementConfig {

	@Autowired
	public void setMapKeyDotReplacement(MappingMongoConverter mongoConverter) {
		mongoConverter.setMapKeyDotReplacement("-");
	}
}

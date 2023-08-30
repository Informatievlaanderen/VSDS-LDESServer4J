package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.config;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.converter.LdesFragmentIdentifierConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class Converters {
	@Bean
	public MongoCustomConversions mongoCustomConversions() {

		return new MongoCustomConversions(List.of(new LdesFragmentIdentifierConverter()));
	}
}

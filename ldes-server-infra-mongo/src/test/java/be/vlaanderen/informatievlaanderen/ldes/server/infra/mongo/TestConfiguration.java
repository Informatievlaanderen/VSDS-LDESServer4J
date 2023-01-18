package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.LdesFragmentMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.MemberMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class TestConfiguration {

	@Bean
	public MemberMongoRepository ldesMemberMongoRepository(
			final LdesMemberEntityRepository ldesMemberEntityRepository, final MongoTemplate mongoTemplate) {
		return new MemberMongoRepository(ldesMemberEntityRepository, mongoTemplate);
	}

	@Bean
	public LdesFragmentMongoRepository ldesFragmentMongoRepository(final LdesFragmentEntityRepository repository,
			final MongoTemplate mongoTemplate) {
		return new LdesFragmentMongoRepository(repository, mongoTemplate);
	}
}

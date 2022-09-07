package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {
	@Bean
	public LdesMemberMongoRepository ldesMemberMongoRepository(
			final LdesMemberEntityRepository ldesMemberEntityRepository) {
		return new LdesMemberMongoRepository(ldesMemberEntityRepository);
	}
}

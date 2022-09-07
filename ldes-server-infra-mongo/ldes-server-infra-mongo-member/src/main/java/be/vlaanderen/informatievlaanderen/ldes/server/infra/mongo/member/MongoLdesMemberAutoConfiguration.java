package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.repository.LdesMemberEntityRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LdesMemberMongoRepository.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class MongoLdesMemberAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public LdesMemberRepository ldesMemberMongoRepository(final LdesMemberEntityRepository ldesMemberEntityRepository) {
		return new LdesMemberMongoRepository(ldesMemberEntityRepository);
	}
}

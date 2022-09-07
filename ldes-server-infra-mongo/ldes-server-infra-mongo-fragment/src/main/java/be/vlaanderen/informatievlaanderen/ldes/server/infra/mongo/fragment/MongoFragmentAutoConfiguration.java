package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LdesFragmentMongoRepository.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class MongoFragmentAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public LdesFragmentRepository ldesFragmentMongoRepository(
			final LdesFragmentEntityRepository ldesFragmentEntityRepository) {
		return new LdesFragmentMongoRepository(ldesFragmentEntityRepository);
	}
}

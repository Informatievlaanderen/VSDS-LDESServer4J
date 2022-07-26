package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.repository.LdesFragmentViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
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
public class MongoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LdesMemberRepository ldesMemberMongoRepository(final LdesMemberEntityRepository ldesMemberEntityRepository) {
        return new LdesMemberMongoRepository(ldesMemberEntityRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public LdesFragmentRespository ldesFragmentMongoRepository(
            final LdesFragmentEntityRepository ldesFragmentEntityRepository,
            final LdesMemberEntityRepository ldesMemberEntityRepository) {
        return new LdesFragmentMongoRepository(ldesFragmentEntityRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public LdesFragmentViewRepository ldesFragmentViewMongoRepository(final LdesFragmentViewEntityRepository ldesFragmentViewEntityRepository) {
        return new LdesFragmentViewMongoRepository(ldesFragmentViewEntityRepository);
    }
}

package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters.LdesMemberConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LdesMemberMongoRepository.class)
@EnableConfigurationProperties()
public class MongoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LdesMemberMongoRepository ldesMemberMongoRepository(
            final LdesMemberEntityRepository ldesMemberEntityRepository) {
        return new LdesMemberMongoRepository(ldesMemberEntityRepository, new LdesMemberConverterImpl());
    }

}

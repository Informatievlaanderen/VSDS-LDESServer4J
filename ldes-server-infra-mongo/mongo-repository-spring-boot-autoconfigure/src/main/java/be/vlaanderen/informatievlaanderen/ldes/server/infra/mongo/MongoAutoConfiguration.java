package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config.EndpointConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.services.LdesFragmentCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LdesMemberMongoRepository.class)
@EnableConfigurationProperties(value = { EndpointProperties.class })
public class MongoAutoConfiguration {

    @Autowired
    private EndpointProperties endpointProperties;

    @Bean
    @ConditionalOnMissingBean
    public EndpointConfig endpointConfig() {
        String endpoint = endpointProperties.getEndpoint() == null ? "localhost" : endpointProperties.getEndpoint();
        return new EndpointConfig(endpoint);
    }

    @Bean
    @ConditionalOnMissingBean
    public LdesMemberMongoRepository ldesFragmentStorageService(final LdesFragmentRepository ldesFragmentRepository,
            final EndpointConfig endpointConfig) {
        final LdesFragmentCreator fragmentCreator = new LdesFragmentCreator(endpointConfig);
        return new LdesMemberMongoRepository(ldesFragmentRepository, fragmentCreator);
    }

}

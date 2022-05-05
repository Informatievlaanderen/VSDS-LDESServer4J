package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config.EndpointConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repository.LdesFragmentMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LdesFragmentStorageService.class)
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
    public LdesFragmentStorageService ldesFragmentStorageService(
            final LdesFragmentMongoRepository ldesFragmentMongoRepository, final EndpointConfig endpointConfig) {
        final LdesFragmentCreator fragmentCreator = new LdesFragmentCreator(endpointConfig);
        return new LdesFragmentStorageService(ldesFragmentMongoRepository, fragmentCreator);
    }

}

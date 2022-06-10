package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {
    @Bean
    public LdesMemberMongoRepository ldesMemberMongoRepository(
            final LdesMemberEntityRepository ldesMemberEntityRepository) {
        return new LdesMemberMongoRepository(ldesMemberEntityRepository);
    }

    @Bean
    public LdesFragmentMongoRepository ldesFragmentMongoRepository(final LdesFragmentEntityRepository repository) {
        return new LdesFragmentMongoRepository(repository);
    }
}

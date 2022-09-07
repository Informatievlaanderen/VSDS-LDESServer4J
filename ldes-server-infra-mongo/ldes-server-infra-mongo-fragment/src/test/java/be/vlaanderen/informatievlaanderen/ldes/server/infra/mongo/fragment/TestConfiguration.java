package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragment.repository.LdesFragmentEntityRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {

	@Bean
	public LdesFragmentMongoRepository ldesFragmentMongoRepository(final LdesFragmentEntityRepository repository) {
		return new LdesFragmentMongoRepository(repository);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesFragmentEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootApplication
public class TestConfiguration {
	@Bean
	public Tracer tracer() {
		Tracer tracer = mock(Tracer.class);
		Span span = mock(Span.class);
		when(tracer.nextSpan()).thenReturn(span);
		when(span.name(anyString())).thenReturn(span);
		when(span.start()).thenReturn(span);

		return tracer;
	}

	@Bean
	public MemberMongoRepository ldesMemberMongoRepository(
			final LdesMemberEntityRepository ldesMemberEntityRepository, MongoTemplate mongoTemplate) {
		return new MemberMongoRepository(ldesMemberEntityRepository, mongoTemplate);
	}

	@Bean
	public LdesFragmentMongoRepository ldesFragmentMongoRepository(final LdesFragmentEntityRepository repository) {
		return new LdesFragmentMongoRepository(repository);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters.LdesMemberConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestionWebConfig {
	@Bean
	public LdesMemberConverter ldesMemberConverter(final EventStreamService eventStreamService) {
		return new LdesMemberConverter(eventStreamService);
	}

}

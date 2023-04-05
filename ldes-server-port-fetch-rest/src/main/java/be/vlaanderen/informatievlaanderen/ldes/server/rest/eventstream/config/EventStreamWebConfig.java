package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class EventStreamWebConfig {

	@Bean
	public HttpMessageConverter<EventStream> eventstreamHttpConverter(
			final EventStreamConverter eventStreamConverter) {
		return new EventStreamHttpConverter(eventStreamConverter);
	}
}

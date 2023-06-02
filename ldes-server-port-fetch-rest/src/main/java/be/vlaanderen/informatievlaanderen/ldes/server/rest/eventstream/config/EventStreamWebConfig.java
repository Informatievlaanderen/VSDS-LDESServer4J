package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class EventStreamWebConfig {

	@Bean
	public HttpMessageConverter<EventStreamResponse> eventStreamResponseHttpMessageConverter(
			EventStreamResponseConverter eventStreamResponseConverter) {
		return new EventStreamResponseHttpConverter(eventStreamResponseConverter);
	}

}

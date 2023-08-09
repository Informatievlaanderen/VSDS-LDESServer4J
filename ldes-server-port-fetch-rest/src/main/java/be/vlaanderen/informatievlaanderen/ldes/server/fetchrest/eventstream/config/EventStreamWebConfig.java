package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.eventstream.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.eventstream.converters.EventStreamResponseHttpConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

	@ConditionalOnMissingBean
	@Bean
	public ModelConverter modelConverter(final PrefixAdder prefixAdder) {
		return new ModelConverter(prefixAdder);
	}

}

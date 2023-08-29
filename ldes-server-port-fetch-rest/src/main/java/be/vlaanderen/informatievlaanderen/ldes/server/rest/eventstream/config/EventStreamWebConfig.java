package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
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
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder) {
		return new HttpModelConverter(prefixAdder);
	}

}

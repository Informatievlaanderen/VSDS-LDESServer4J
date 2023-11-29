package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;

@Configuration
public class EventStreamWebConfig {

	@Bean
	public HttpMessageConverter<EventStreamResponse> eventStreamResponseHttpMessageConverter(
			EventStreamResponseConverter eventStreamResponseConverter,
			RequestContextExtracter requestContextExtracter,
			@Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		return new EventStreamResponseHttpConverter(eventStreamResponseConverter, requestContextExtracter, useRelativeUrl);
	}

	@ConditionalOnMissingBean
	@Bean
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder) {
		return new HttpModelConverter(prefixAdder);
	}

}

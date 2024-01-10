package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class EventStreamWebConfig {
	public static final String DEFAULT_RDF_MEDIA_TYPE = "text/turtle";

	@Bean
	public HttpMessageConverter<EventStreamResponse> eventStreamResponseHttpMessageConverter(
			EventStreamResponseConverter eventStreamResponseConverter, RdfModelConverter rdfModelConverter) {
		return new EventStreamResponseHttpConverter(eventStreamResponseConverter, rdfModelConverter);
	}

	@ConditionalOnMissingBean
	@Bean
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder, final RdfModelConverter rdfModelConverter) {
		return new HttpModelConverter(prefixAdder, rdfModelConverter);
	}

}

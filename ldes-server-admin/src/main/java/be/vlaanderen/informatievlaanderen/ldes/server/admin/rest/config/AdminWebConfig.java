package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamListHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ListViewHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;

@Configuration
public class AdminWebConfig {

	@ConditionalOnMissingBean
	@Bean
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder) {
		return new HttpModelConverter(prefixAdder);
	}

	@Bean
	public EventStreamHttpConverter eventStreamHttpConverter(
			final EventStreamResponseConverter eventStreamResponseConverter,
			RequestContextExtracter requestContextExtracter,
			@Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		return new EventStreamHttpConverter(eventStreamResponseConverter, requestContextExtracter, useRelativeUrl);
	}

	@Bean
	public EventStreamListHttpConverter eventStreamListHttpConverter(
			final EventStreamResponseConverter eventStreamResponseConverter,
			RequestContextExtracter requestContextExtracter,
			@Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		return new EventStreamListHttpConverter(eventStreamResponseConverter, requestContextExtracter, useRelativeUrl);
	}

	@Bean
	public ViewHttpConverter viewHttpConverter(
			final ViewSpecificationConverter viewSpecificationConverter,
			RequestContextExtracter requestContextExtracter,
			@Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		return new ViewHttpConverter(viewSpecificationConverter, requestContextExtracter, useRelativeUrl);
	}

	@Bean
	public ListViewHttpConverter listViewHttpConverter(
			final ViewSpecificationConverter viewSpecificationConverter,
			RequestContextExtracter requestContextExtracter,
			@Value(USE_RELATIVE_URL_KEY) Boolean useRelativeUrl) {
		return new ListViewHttpConverter(viewSpecificationConverter, requestContextExtracter, useRelativeUrl);
	}
}

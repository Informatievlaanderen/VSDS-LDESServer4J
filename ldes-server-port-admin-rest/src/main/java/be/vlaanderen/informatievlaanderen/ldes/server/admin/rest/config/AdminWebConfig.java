package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamListHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ListViewHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {

	@ConditionalOnMissingBean
	@Bean
	public ModelConverter modelConverter(final PrefixAdder prefixAdder) {
		return new ModelConverter(prefixAdder);
	}

	@Bean
	public EventStreamHttpConverter eventStreamHttpConverter(
			final EventStreamResponseConverter eventStreamResponseConverter) {
		return new EventStreamHttpConverter(eventStreamResponseConverter);
	}

	@Bean
	public EventStreamListHttpConverter eventStreamListHttpConverter(
			final EventStreamResponseConverter eventStreamResponseConverter) {
		return new EventStreamListHttpConverter(eventStreamResponseConverter);
	}

	@Bean
	public ViewHttpConverter viewHttpConverter(final ViewSpecificationConverter viewSpecificationConverter) {
		return new ViewHttpConverter(viewSpecificationConverter);
	}

	@Bean
	public ListViewHttpConverter listViewHttpConverter(final ViewSpecificationConverter viewSpecificationConverter) {
		return new ListViewHttpConverter(viewSpecificationConverter);
	}
}

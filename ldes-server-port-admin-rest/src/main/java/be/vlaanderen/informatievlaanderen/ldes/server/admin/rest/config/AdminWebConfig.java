package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ShaclShapeValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ViewValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {

	@Bean
	public ModelConverter modelConverter() {
		return new ModelConverter();
	}

	@Bean
	public EventStreamListHttpConverter eventStreamListHttpConverter() {
		return new EventStreamListHttpConverter();
	}

	@Bean
	public EventStreamHttpConverter eventStreamHttpConverter() {
		return new EventStreamHttpConverter();
	}

	@Bean
	public ViewHttpConverter viewHttpConverter(final ViewSpecificationConverter viewSpecificationConverter) {
		return new ViewHttpConverter(viewSpecificationConverter);
	}

	@Bean
	public ListViewHttpConverter listViewHttpConverter(final ViewSpecificationConverter viewSpecificationConverter) {
		return new ListViewHttpConverter(viewSpecificationConverter);
	}

	@Bean
	public ViewValidator viewValidator() {
		return new ViewValidator();
	}

	@Bean
	public ShaclShapeValidator shaclShapeValidator() {
		return new ShaclShapeValidator();
	}

	@Bean
	public EventStreamValidator eventStreamValidator() {
		return new EventStreamValidator();
	}
}

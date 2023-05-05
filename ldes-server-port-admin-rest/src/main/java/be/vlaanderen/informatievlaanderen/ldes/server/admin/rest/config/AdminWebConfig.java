package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ShaclShapeValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {

	@Bean
	public ModelConverter modelConverter() {
		return new ModelConverter();
	}

	@Bean
	public EventStreamListHttpConverter eventStreamListHttpConverter(final AppConfig appConfig) {
		return new EventStreamListHttpConverter(appConfig);
	}

	@Bean
	public EventStreamHttpConverter eventStreamHttpConverter(final AppConfig appConfig) {
		return new EventStreamHttpConverter(appConfig);
	}

	@Bean
	public ViewHttpConverter viewHttpConverter() {
		return new ViewHttpConverter();
	}

	@Bean
	public ListViewHttpConverter listViewHttpConverter() {
		return new ListViewHttpConverter();
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

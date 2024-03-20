package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ShaclValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorsConfig {
	@Bean("eventStreamShaclValidator")
	public ModelValidator eventStreamShaclValidator(@Value("${ldes-server.eventstream-shape:eventstreamShaclShape.ttl}") String eventStreamShapeUri) {
		return new ShaclValidator(eventStreamShapeUri);
	}

	@Bean("viewShaclValidator")
	public ModelValidator viewShaclValidator(@Value("${ldes-server.view-shape:viewShaclShape.ttl}") String viewShaclShapeUri) {
		return new ShaclValidator(viewShaclShapeUri);
	}

	@Bean("shaclShapeShaclValidator")
	public ModelValidator shaclShapeShaclValidator(@Value("${ldes-server.shacl-shape-shacl:shapeShaclShape.ttl}") String shapeShaclShapeUri) {
		return new ShaclValidator(shapeShaclShapeUri);
	}
}

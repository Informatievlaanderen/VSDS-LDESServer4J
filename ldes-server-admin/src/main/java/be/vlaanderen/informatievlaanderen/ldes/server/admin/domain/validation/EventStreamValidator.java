package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventStreamValidator extends AbstractShaclValidator {
	public static final String FILE_NAME = "eventstreamShaclShape.ttl";

	@Override
	protected void initializeShapes() {
		shapes = Shapes.parse(FILE_NAME);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Model.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Model eventStream = (Model) target;
		validateShape(eventStream);
	}
}

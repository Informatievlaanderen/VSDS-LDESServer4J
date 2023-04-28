package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class EventStreamValidator extends AbstractShaclValidator {
	@Override
	protected void initializeShapes() {
		shapes = Shapes.parse("eventstreamShaclShape.ttl");
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

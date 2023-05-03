package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class EventStreamValidator extends AbstractShaclValidator {
	private static final String FILE_NAME = "eventstreamShaclShape.ttl";
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

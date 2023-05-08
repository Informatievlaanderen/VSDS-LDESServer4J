package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class ViewValidator extends AbstractShaclValidator {
	// Todo: add shape
	private static final String FILE_NAME = "";

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
		// Waiting for shape to validate view
	}
}

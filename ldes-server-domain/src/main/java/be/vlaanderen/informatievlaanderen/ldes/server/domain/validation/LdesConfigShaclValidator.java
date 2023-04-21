package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class LdesConfigShaclValidator extends AbstractShaclValidator {
	private final String fileName;

	public LdesConfigShaclValidator(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Model.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Model model = (Model) target;
		validateShape(model);
	}

	@Override
	protected void initializeShapes() {
		shapes = Shapes.parse(fileName);
	}
}

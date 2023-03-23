package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.shacl.Shapes;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class LdesConfigShaclValidator extends AbstractShaclValidator {
	private final String fileName;

	public LdesConfigShaclValidator(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return LdesConfigModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LdesConfigModel ldesConfigModel = (LdesConfigModel) target;
		validateShape(ldesConfigModel.getModel());
	}

	@Override
	protected void initializeShapes() {
		shapes = Shapes.parse(fileName);
	}
}

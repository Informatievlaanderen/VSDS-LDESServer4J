package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class LdesStreamShaclValidator extends AbstractShaclValidator {
	private final String shape;

	public LdesStreamShaclValidator(String shape) {
		this.shape = shape;
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
		shapes = Shapes.parse(RDFDataMgr.loadGraph(shape));
	}
}

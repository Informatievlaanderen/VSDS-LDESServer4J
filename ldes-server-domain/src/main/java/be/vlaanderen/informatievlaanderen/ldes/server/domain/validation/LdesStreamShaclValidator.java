package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;
import org.springframework.validation.Errors;

public class LdesStreamShaclValidator implements Validator {
	private final Shapes shapes;

	public LdesStreamShaclValidator(String shape) {
		shapes = Shapes.parse(RDFDataMgr.loadGraph(shape));
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
}

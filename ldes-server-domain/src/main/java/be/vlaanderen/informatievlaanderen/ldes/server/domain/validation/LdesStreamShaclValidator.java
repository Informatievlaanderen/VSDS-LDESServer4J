package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesStreamShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesStreamModel;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class LdesStreamShaclValidator extends AbstractShaclValidator {
	private final String shape;

	public LdesStreamShaclValidator(String shape) {
		this.shape = shape;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return LdesStreamModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LdesStreamModel ldesStreamModel = (LdesStreamModel) target;
		validateShape(ldesStreamModel);
	}

	protected void validateShape(LdesStreamModel ldesStreamModel) {
		ValidationReport report = ShaclValidator.get().validate(shapes, ldesStreamModel.getModel().getGraph());

		if (!report.conforms()) {
			throw new LdesStreamShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE));
		}
	}

	@Override
	protected void initializeShapes() {
		shapes = Shapes.parse(RDFDataMgr.loadGraph(shape));
	}
}

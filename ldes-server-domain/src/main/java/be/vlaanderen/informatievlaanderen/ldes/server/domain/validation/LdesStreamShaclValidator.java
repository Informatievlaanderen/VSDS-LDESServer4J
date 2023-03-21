package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesStreamShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.LdesConfigModel;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
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
		validateShape(ldesConfigModel);
	}

	protected void validateShape(LdesConfigModel ldesConfigModel) {
		ValidationReport report = ShaclValidator.get().validate(shapes, ldesConfigModel.getModel().getGraph());

		if (!report.conforms()) {
			throw new LdesStreamShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE));
		}
	}
}

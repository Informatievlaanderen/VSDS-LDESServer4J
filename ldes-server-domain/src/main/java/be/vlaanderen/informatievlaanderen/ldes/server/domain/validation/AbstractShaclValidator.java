package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.validation.Validator;

public abstract class AbstractShaclValidator implements Validator {
	private boolean initialized;
	protected Shapes shapes;

	protected abstract void initializeShapes();

	protected void validateShape(Model model) {
		if (!initialized) {
			initializeShapes();
			initialized = true;
		}
		if (shapes != null) {
			ValidationReport report = ShaclValidator.get().validate(shapes, model.getGraph());

			if (!report.conforms()) {
				throw new LdesShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE));
			}
		}
	}
}

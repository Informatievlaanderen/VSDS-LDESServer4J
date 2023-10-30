package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractShaclValidator implements ModelValidator {
	private boolean initialized;
	protected Shapes shapes;

	protected abstract void initializeShapes();

	@Override
	public void validate(@NotNull Model target) {
		if (!initialized) {
			initializeShapes();
			initialized = true;
		}
		if (shapes != null) {
			ValidationReport report = ShaclValidator.get().validate(shapes, target.getGraph());

			if (!report.conforms()) {
				throw new LdesShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE),
						report.getModel());
			}
		}
	}
}

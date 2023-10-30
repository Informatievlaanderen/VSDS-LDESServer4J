package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Validator;

public abstract class AbstractShaclValidator implements Validator {
	private boolean initialized;
	protected Shapes shapes;

	@Override
	public boolean supports(@NotNull Class<?> clazz) {
		return Model.class.isAssignableFrom(clazz);
	}

	protected abstract void initializeShapes();

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

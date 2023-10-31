package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShaclValidator implements ModelValidator {
	protected final String shapesFileUri;
	private boolean initialized;
	protected Shapes shapes;

	public ShaclValidator(String shapesFileUri) {
		this.shapesFileUri = shapesFileUri;
	}

	@Override
	public void validate(@NotNull Model target) {
		if (!initialized) {
			initializeShapes();
			initialized = true;
		}
		if (shapes != null) {
			ValidationReport report = org.apache.jena.shacl.ShaclValidator.get().validate(shapes, target.getGraph());

			if (!report.conforms()) {
				throw new LdesShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE),
						report.getModel());
			}
		}
	}

	protected void initializeShapes() {
		shapes = Shapes.parse(Objects.requireNonNull(shapesFileUri));
	}
}

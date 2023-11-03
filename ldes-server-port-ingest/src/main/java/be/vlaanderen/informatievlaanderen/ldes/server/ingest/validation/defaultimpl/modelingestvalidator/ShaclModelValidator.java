package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;

public class ShaclModelValidator implements ModelIngestValidator {

	private final Shapes shapes;

	public ShaclModelValidator(Shapes shapes) {
		this.shapes = shapes;
	}

	@Override
	public void validate(Model model) {
		ValidationReport report = ShaclValidator.get().validate(shapes, model.getGraph());
		if (!report.conforms()) {
			throw new ShaclValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE), report.getModel());
		}
	}

}

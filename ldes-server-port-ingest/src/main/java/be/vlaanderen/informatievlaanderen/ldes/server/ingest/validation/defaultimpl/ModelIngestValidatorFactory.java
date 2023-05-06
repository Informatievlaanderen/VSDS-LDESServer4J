package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.EmptyValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ShaclModelValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.Shapes;

public class ModelIngestValidatorFactory {

	private final AppConfig appConfig;

	public ModelIngestValidatorFactory(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	public ModelIngestValidator createValidator(final Model shaclShape, final String collectionName) {
		final LdesConfig.Validation validationConfig = appConfig.getLdesConfig(collectionName).validation();

		if (!validationConfig.isEnabled()) {
			return new EmptyValidator();
		}

		if (shaclShape != null) {
			return new ShaclModelValidator(Shapes.parse(shaclShape));
		}

		if (validationConfig.getShape() != null) {
			return new ShaclModelValidator(Shapes.parse(RDFDataMgr.loadGraph(validationConfig.getShape())));
		}

		return new EmptyValidator();
	}

}

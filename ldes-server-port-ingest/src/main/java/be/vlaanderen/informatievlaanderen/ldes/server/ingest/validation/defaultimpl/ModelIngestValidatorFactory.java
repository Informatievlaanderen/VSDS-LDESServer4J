package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.IngestValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;

public class ModelIngestValidatorFactory {

    private final AppConfig appConfig;

    public ModelIngestValidatorFactory(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    ModelIngestValidator createValidator(final Model shaclShape, final String collectionName) {
        final LdesConfig.Validation validationConfig = appConfig.getLdesConfig(collectionName).validation();

        if (!validationConfig.isEnabled()) {
            return emptyValidator();
        }

        if (shaclShape != null) {
            return shaclValidator(Shapes.parse(shaclShape));
        }

        if (validationConfig.getShape() != null) {
            return shaclValidator(Shapes.parse(RDFDataMgr.loadGraph(validationConfig.getShape())));
        }

        return emptyValidator();
    }

    private static ModelIngestValidator emptyValidator() {
        return model -> {};
    }

    private static ModelIngestValidator shaclValidator(Shapes shapes) {
        return model -> {
            ValidationReport report = ShaclValidator.get().validate(shapes, model.getGraph());
            if (!report.conforms()) {
                throw new IngestValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE));
            }
        };
    }

}

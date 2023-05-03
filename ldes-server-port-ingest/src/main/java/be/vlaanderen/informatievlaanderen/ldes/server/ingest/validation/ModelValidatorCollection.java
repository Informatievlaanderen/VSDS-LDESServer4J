package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;

import java.util.HashMap;
import java.util.Map;

public class ModelValidatorCollection {

    private final AppConfig appConfig;
    private final ShaclCollection shaclCollection;
    private final Map<String, ModelValidator> validators = new HashMap<>();

    public ModelValidatorCollection(AppConfig appConfig, ShaclCollection shaclCollection) {
        this.appConfig = appConfig;
        this.shaclCollection = shaclCollection;
    }

    ModelValidator retrieveValidator(String collectionName) {
        return validators.computeIfAbsent(collectionName, this::createValidator);
    }

    private ModelValidator createValidator(String collectionName) {
        final LdesConfig.Validation validationConfig = appConfig.getLdesConfig(collectionName).validation();

        return shaclCollection
                .retrieveShape(collectionName)
                .map(shaclShape -> from(shaclShape.getModel(), validationConfig))
                .orElse(emptyValidator());
    }

    private ModelValidator from(final Model shaclShape, LdesConfig.Validation validationConfig) {
        if (validationConfig.isEnabled()) {
            if (shaclShape != null) {
                return shaclValidator(Shapes.parse(shaclShape));
            }

            if (validationConfig.getShape() != null) {
                return shaclValidator(Shapes.parse(RDFDataMgr.loadGraph(validationConfig.getShape())));
            }
        }

        return emptyValidator();
    }

    private static ModelValidator emptyValidator() {
        return model -> {};
    }

    private static ModelValidator shaclValidator(Shapes shapes) {
        return model -> {
            ValidationReport report = ShaclValidator.get().validate(shapes, model.getGraph());
            if (!report.conforms()) {
                throw new IngestValidationException(RdfModelConverter.toString(report.getModel(), Lang.TURTLE));
            }
        };
    }

}

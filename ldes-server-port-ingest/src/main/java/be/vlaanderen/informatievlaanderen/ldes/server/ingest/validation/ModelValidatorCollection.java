package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

public class ModelValidatorCollection {

    private final AppConfig appConfig;
    private final Map<String, ModelValidator> validators = new HashMap<>();

    public ModelValidatorCollection(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    ModelValidator retrieveValidator(String collectionName) {
        return validators.get(collectionName);
    }

    @EventListener
    public void handleShaclChangedEvent(ShaclChangedEvent event) {
        final ShaclShape shacl = event.getShacl();
        final String collectionName = shacl.getCollection();
        final LdesConfig.Validation validationConfig = appConfig.getLdesConfig(collectionName).validation();

        validators.compute(collectionName, (key, oldValue) -> from(shacl.getModel(), validationConfig));
    }

    private ModelValidator from(final Model shaclShape, LdesConfig.Validation validationConfig) {
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

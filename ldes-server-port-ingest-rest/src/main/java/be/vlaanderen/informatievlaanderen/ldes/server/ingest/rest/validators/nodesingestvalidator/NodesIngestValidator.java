package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.nodesingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.IngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ShaclModelValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.shacl.Shapes;

public class NodesIngestValidator implements IngestValidator {
    private static final String SHACLSHAPE_FILE_URI = "nodes-shacl.ttl";
    private final ShaclModelValidator validator;

    public NodesIngestValidator() {
        Shapes shapes = Shapes.parse(SHACLSHAPE_FILE_URI);
        validator = new ShaclModelValidator(shapes);
    }

    @Override
    public void validate(Model model, String collectionName) {
        validator.validate(model);
    }
}

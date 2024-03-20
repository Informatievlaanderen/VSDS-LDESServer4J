package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.nodesingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ShaclModelValidator;
import org.apache.jena.shacl.Shapes;

public class NodesIngestValidatorFactory {

    private static final String SHACLSHAPE_FILE_URI = "";
    public ModelIngestValidator createValidator() {
        Shapes shapes = Shapes.parse(SHACLSHAPE_FILE_URI);
        return new ShaclModelValidator(shapes);
    }
}

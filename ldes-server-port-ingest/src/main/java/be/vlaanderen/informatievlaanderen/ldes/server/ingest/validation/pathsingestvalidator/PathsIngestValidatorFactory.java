package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.pathsingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ShaclModelValidator;
import org.apache.jena.shacl.Shapes;

public class PathsIngestValidatorFactory {

    private static final String VERSION_SHAPE_FILE_URI = "";
    private static final String STATE_SHAPE_FILE_URI = "";

    public ModelIngestValidator createValidator() {
        Shapes shapesVersion = Shapes.parse(VERSION_SHAPE_FILE_URI);
        ModelIngestValidator versionValidator = new ShaclModelValidator(shapesVersion);

        Shapes shapesState = Shapes.parse(STATE_SHAPE_FILE_URI);
        ModelIngestValidator stateValidator = new ShaclModelValidator(shapesState);
        return new PathsIngestValidator(versionValidator, stateValidator);
    }
}

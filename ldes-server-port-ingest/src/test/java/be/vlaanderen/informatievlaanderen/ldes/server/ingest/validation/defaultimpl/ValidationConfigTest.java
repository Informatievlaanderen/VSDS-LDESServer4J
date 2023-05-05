package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationConfigTest {

    @Test
    void test_configCanLoad() {
        ValidationConfig validationConfig = new ValidationConfig();
        var factory = assertDoesNotThrow(() -> validationConfig.modelIngestValidatorFactory(new AppConfig()));
        assertDoesNotThrow(() -> validationConfig.ingestValidatorCollection(factory));
    }

}
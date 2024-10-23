package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ValidationConfigTest {

	@Test
	void test_configCanLoad() {
		ValidationConfig validationConfig = new ValidationConfig();
		var factory = assertDoesNotThrow(validationConfig::modelIngestValidatorFactory);
		assertDoesNotThrow(() -> validationConfig.ingestValidatorCollection(factory));
	}

}
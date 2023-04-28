package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IngestRestConfigTest {

	@Test
	void triggerEspgDatabaseInitializationOnStartupShouldNotFail() {
		assertDoesNotThrow(() -> new IngestRestConfig().triggerEspgDatabaseInitializationOnStartup());
	}

}
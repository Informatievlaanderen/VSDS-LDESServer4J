package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IngestValidationExceptionTest {

	@Test
	void testMessageIsCorrect() {
		assertEquals("Validation failed: \n\nmsg", new IngestValidationException("msg").getMessage());
	}

}
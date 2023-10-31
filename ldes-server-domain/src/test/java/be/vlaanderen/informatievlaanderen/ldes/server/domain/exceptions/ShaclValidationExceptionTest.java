package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShaclValidationExceptionTest {
	@Test
	void test_CorrectErrorMessage() {
		assertThat(new ShaclValidationException("msg", null)).hasMessage("Shacl validation failed: \n\nmsg");
	}
}
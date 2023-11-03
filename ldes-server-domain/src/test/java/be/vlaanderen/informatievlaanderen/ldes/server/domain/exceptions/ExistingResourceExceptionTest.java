package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExistingResourceExceptionTest {
	@Test
	void test_CorrectErrorMessage() {
		assertThat(new ExistingResourceException("resource", "resource-id"))
				.hasMessage("Resource of type: resource with id: resource-id already exists.");
	}
}
package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.propertypath;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyPropertyExtractorTest {

	@Test
	void getObjects() {
		assertTrue(new EmptyPropertyExtractor().getProperties(null).isEmpty());
	}

}
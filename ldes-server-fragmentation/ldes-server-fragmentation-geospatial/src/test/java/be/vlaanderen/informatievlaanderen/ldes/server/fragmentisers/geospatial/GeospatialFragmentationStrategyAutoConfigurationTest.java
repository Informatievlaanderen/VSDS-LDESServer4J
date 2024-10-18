package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GeospatialFragmentationStrategyAutoConfigurationTest {

	@Test
	void triggerEspgDatabaseInitializationOnStartupShouldNotFail() {
		assertDoesNotThrow(() -> new GeospatialFragmentationStrategyAutoConfiguration()
				.triggerEspgDatabaseInitializationOnStartup());
	}

}
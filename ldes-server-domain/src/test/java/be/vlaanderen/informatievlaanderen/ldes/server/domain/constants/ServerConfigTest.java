package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigTest {
	@Test
	void when_PropertiesAreFilledIn_TheyCanBeConsulted() {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setHostName("LOCALHOST");
		serverConfig.setCompactionDuration("PT1M");

		assertEquals("LOCALHOST", serverConfig.getHostName());
		assertEquals("PT1M", serverConfig.getCompactionDuration());
	}

	@Test
	void when_PropertiesAreEmpty_NullOrDefaultValuesAreReturned() {
		ServerConfig serverConfig = new ServerConfig();

		assertNull(serverConfig.getHostName());
		assertEquals("P7D", serverConfig.getCompactionDuration());
	}

}
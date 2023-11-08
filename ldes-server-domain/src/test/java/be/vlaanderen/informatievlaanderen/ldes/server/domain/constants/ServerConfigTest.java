package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServerConfigTest {
	@Test
	void when_PropertiesAreFilledIn_TheyCanBeConsulted() {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setHostName("LOCALHOST");
		serverConfig.setCompactionDuration("PT1M");
		serverConfig.setRetentionCron("*/20 * * * * *");
		serverConfig.setDeletionCron("*/30 * * * * *");
		serverConfig.setCompactionCron("*/45 * * * * *");

		assertThat(serverConfig.getHostName()).isEqualTo("LOCALHOST");
		assertThat(serverConfig.getCompactionDuration()).isEqualTo("PT1M");
		assertThat(serverConfig.getRetentionCron()).isEqualTo("*/20 * * * * *");
		assertThat(serverConfig.getDeletionCron()).isEqualTo("*/30 * * * * *");
		assertThat(serverConfig.getCompactionCron()).isEqualTo("*/45 * * * * *");
	}

	@Test
	void when_PropertiesAreEmpty_NullOrDefaultValuesAreReturned() {
		final String backgroundCron = "0 0 0 * * *";
		ServerConfig serverConfig = new ServerConfig();

		assertThat(serverConfig.getHostName()).isNull();
		assertThat(serverConfig.getCompactionDuration()).isEqualTo("P7D");
		assertThat(serverConfig.getRetentionCron()).isEqualTo(backgroundCron);
		assertThat(serverConfig.getDeletionCron()).isEqualTo(backgroundCron);
		assertThat(serverConfig.getCompactionCron()).isEqualTo(backgroundCron);
	}

}
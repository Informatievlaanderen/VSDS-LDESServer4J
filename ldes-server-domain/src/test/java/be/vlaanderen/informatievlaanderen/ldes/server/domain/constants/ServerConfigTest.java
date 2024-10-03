package be.vlaanderen.informatievlaanderen.ldes.server.domain.constants;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServerConfigTest {
	@Test
	void when_PropertiesAreFilledIn_TheyCanBeConsulted() {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setHostName("LOCALHOST");
		serverConfig.setUseRelativeUrl(true);
		serverConfig.setCompactionDuration("PT1M");
		serverConfig.setFragmentationCron("*/20 * * * * *");
		serverConfig.setMaintenanceCron("*/30 * * * * *");

		assertThat(serverConfig.getHostName()).isEqualTo("LOCALHOST");
		assertThat(serverConfig.getUseRelativeUrl()).isTrue();
		assertThat(serverConfig.getCompactionDuration()).isEqualTo("PT1M");
		assertThat(serverConfig.getFragmentationCron()).isEqualTo("*/20 * * * * *");
		assertThat(serverConfig.getMaintenanceCron()).isEqualTo("*/30 * * * * *");
	}

	@Test
	void when_PropertiesAreEmpty_NullOrDefaultValuesAreReturned() {
		final String backgroundCron = "0 0 0 * * *";
		final String fragmentationCron = "*/30 * * * * *";
		ServerConfig serverConfig = new ServerConfig();

		assertThat(serverConfig.getHostName()).isNull();
		assertThat(serverConfig.getUseRelativeUrl()).isFalse();
		assertThat(serverConfig.getCompactionDuration()).isEqualTo("P7D");
		assertThat(serverConfig.getMaintenanceCron()).isEqualTo(backgroundCron);
		assertThat(serverConfig.getFragmentationCron()).isEqualTo(fragmentationCron);
	}

}
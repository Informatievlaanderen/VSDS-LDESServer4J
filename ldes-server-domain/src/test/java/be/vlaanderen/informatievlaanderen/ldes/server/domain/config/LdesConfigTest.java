package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { LdesConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("test")
class LdesConfigTest {

	@Autowired
	private LdesConfig ldesConfig;

	@Test
	@DisplayName("Verify content of LdesConfig")
	void when_LdesPropertiesAreInjected_TheyCanBeConsultedViaLdesConfig() {
		assertEquals("exampleData", ldesConfig.getCollectionName());
		assertEquals("http://localhost:8089", ldesConfig.getHostName());
		assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				ldesConfig.validation().getShape());
		assertEquals("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", ldesConfig.getMemberType());
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", ldesConfig.getTimestampPath());
		assertEquals("http://purl.org/dc/terms/isVersionOf", ldesConfig.getVersionOfPath());
	}
}
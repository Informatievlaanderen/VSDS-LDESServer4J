package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { FragmentationConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("fragmentation-config-test")
class FragmentationConfigTest {

	@Autowired
	private FragmentationConfig fragmentationConfig;

	@Test
	@DisplayName("Verify content of FragmentationConfig")
	void when_FragmentationPropertiesAreInjected_TheyCanBeConsultedViaFragmentationConfig() {
		assertEquals("geospatial", fragmentationConfig.getName());
		Map<String, String> geospatialConfig = Map.of("maxZoomLevel", "15", "bucketiserProperty",
				"http://www.opengis.net/ont/geosparql#asWKT",
				"projection", "lambert72");
		assertEquals(geospatialConfig, fragmentationConfig.getConfig());
		assertEquals("timebased", fragmentationConfig.getFragmentation().getName());
		Map<String, String> timebasedConfig = Map.of("memberLimit", "5");
		assertEquals(timebasedConfig, fragmentationConfig.getFragmentation().getConfig());
		assertNull(fragmentationConfig.getFragmentation().getFragmentation());
	}
}
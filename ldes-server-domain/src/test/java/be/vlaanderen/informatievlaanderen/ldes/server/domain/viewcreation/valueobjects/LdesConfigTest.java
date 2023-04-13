package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.PathToModelConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = { LdesConfig.class, PathToModelConverter.class })
@EnableConfigurationProperties
@ActiveProfiles("test")
class LdesConfigTest {

	@Autowired
	private LdesConfig ldesConfig;

	@Test
	@DisplayName("Verify content of LdesConfig")
	void when_LdesPropertiesAreInjected_TheyCanBeConsultedViaLdesConfig() {
		verifyFirstLdes(ldesConfig.getCollections().get(0));
		verifySecondLdes(ldesConfig.getCollections().get(1));

	}

	private void verifySecondLdes(LdesSpecification ldesSpecification) {
		assertEquals("http://localhost:8088", ldesSpecification.getHostName());
		assertEquals("ldes-2", ldesSpecification.getCollectionName());
		assertEquals("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", ldesSpecification.getMemberType());
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", ldesSpecification.getTimestampPath());
		assertNull(ldesSpecification.getVersionOfPath());
		assertNull(ldesSpecification.validation().getShape());
		assertTrue(ldesSpecification.validation().isEnabled());
		assertTrue(ldesSpecification.getDcat().isEmpty());
		verifyDefaultViewSecondLdes(ldesSpecification.getViews());
	}

	private void verifyDefaultViewSecondLdes(List<ViewSpecification> views) {
		assertEquals(1, views.size());
		ViewSpecification firstViewSpecification = views.get(0);
		assertEquals("ldes-2/firstView", firstViewSpecification.getName());
		assertEquals(1, firstViewSpecification.getFragmentations().size());
		assertEquals(List.of(), firstViewSpecification.getRetentionConfigs());
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(0), "pagination",
				Map.of("memberLimit", "21"));
	}

	void verifyFirstLdes(LdesSpecification ldesSpecification) {
		assertEquals("ldes-1", ldesSpecification.getCollectionName());
		assertEquals("http://localhost:8089", ldesSpecification.getHostName());
		assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				ldesSpecification.validation().getShape());
		assertEquals("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", ldesSpecification.getMemberType());
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", ldesSpecification.getTimestampPath());
		assertEquals("http://purl.org/dc/terms/isVersionOf", ldesSpecification.getVersionOfPath());
		assertFalse(ldesSpecification.getDcat().isEmpty());
		verifyViewsFirstLdes(ldesSpecification.getViews());
	}

	void verifyViewsFirstLdes(List<ViewSpecification> views) {
		assertEquals(3, views.size());

		ViewSpecification firstViewSpecification = views.get(0);
		assertEquals("ldes-1/firstView", firstViewSpecification.getName());
		assertEquals(2, firstViewSpecification.getFragmentations().size());
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(0), "geospatial",
				Map.of("maxZoomLevel", "15", "fragmenterProperty", "http://www.opengis.net/ont/geosparql#asWKT"));
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(1), "timebased",
				Map.of("memberLimit", "5"));
		assertEquals(1, firstViewSpecification.getRetentionConfigs().size());
		RetentionConfig retentionConfig = firstViewSpecification.getRetentionConfigs().get(0);
		verifyRetentionPolicy(retentionConfig);

		ViewSpecification secondViewSpecification = views.get(1);
		assertEquals("ldes-1/secondView", secondViewSpecification.getName());
		assertEquals(1, secondViewSpecification.getFragmentations().size());
		assertEquals(List.of(), secondViewSpecification.getRetentionConfigs());
		verifyViewSpecification(secondViewSpecification.getFragmentations().get(0), "timebased",
				Map.of("memberLimit", "3"));

		ViewSpecification defaultViewSpecification = views.get(2);
		assertEquals("ldes-1/by-page", defaultViewSpecification.getName());
		assertEquals(1, defaultViewSpecification.getFragmentations().size());
		assertEquals(List.of(), defaultViewSpecification.getRetentionConfigs());
		verifyViewSpecification(defaultViewSpecification.getFragmentations().get(0), "pagination",
				Map.of("memberLimit", "100", "bidirectionalRelations", "false"));
	}

	private void verifyRetentionPolicy(RetentionConfig retentionConfig) {
		assertEquals("timebased", retentionConfig.getName());
		assertEquals(new ConfigProperties(Map.of("duration", "PT1M")), retentionConfig.getProperties());
	}

	private void verifyViewSpecification(FragmentationConfig fragmentationConfig, String fragmentationName,
			Map<String, String> configProperties) {
		assertEquals(fragmentationName, fragmentationConfig.getName());
		assertEquals(new ConfigProperties(configProperties), fragmentationConfig.getProperties());
	}

}
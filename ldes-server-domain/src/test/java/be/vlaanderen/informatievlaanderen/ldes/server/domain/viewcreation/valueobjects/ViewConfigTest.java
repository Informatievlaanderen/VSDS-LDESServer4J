package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = { ViewConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("view-config-test")
class ViewConfigTest {

	@Autowired
	private ViewConfig viewConfig;

	@Test
	@DisplayName("Verify content of ViewConfig")
	void when_ViewConfigIsInjected_ViewSpecificationsCanBeConsulted() {
		assertEquals(2, viewConfig.getViews().size());

		ViewSpecification firstViewSpecification = viewConfig.getViews().get(0);
		assertEquals("firstView", firstViewSpecification.getName());
		assertEquals(2, firstViewSpecification.getFragmentations().size());
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(0), "geospatial",
				Map.of("maxZoomLevel", "15", "fragmenterProperty", "http://www.opengis.net/ont/geosparql#asWKT"));
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(1), "timebased",
				Map.of("memberLimit", "5"));
		assertEquals(1, firstViewSpecification.getRetentionPolicies().size());
		RetentionConfig retentionConfig = firstViewSpecification.getRetentionPolicies().get(0);
		verifyRetentionPolicy(retentionConfig);

		ViewSpecification secondViewSpecification = viewConfig.getViews().get(1);
		assertEquals("secondView", secondViewSpecification.getName());
		assertEquals(1, secondViewSpecification.getFragmentations().size());
		assertNull(secondViewSpecification.getRetentionPolicies());
		verifyViewSpecification(secondViewSpecification.getFragmentations().get(0), "timebased",
				Map.of("memberLimit", "3"));
	}

	private void verifyRetentionPolicy(RetentionConfig retentionConfig) {
		assertEquals("timebased", retentionConfig.getRetentionPolicyName());
		assertEquals(new ConfigProperties(Map.of("durationInSeconds", "100")), retentionConfig.getProperties());
	}

	private void verifyViewSpecification(FragmentationConfig fragmentationConfig, String geospatial,
			Map<String, String> maxZoomLevel) {
		assertEquals(geospatial, fragmentationConfig.getName());
		assertEquals(new ConfigProperties(maxZoomLevel), fragmentationConfig.getProperties());
	}
}
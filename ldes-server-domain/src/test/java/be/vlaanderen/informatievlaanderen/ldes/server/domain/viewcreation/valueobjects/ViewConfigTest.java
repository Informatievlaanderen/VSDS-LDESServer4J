package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { ViewConfig.class })
@EnableConfigurationProperties
@ActiveProfiles("view-config-test")
class ViewConfigTest {

	@Autowired
	private ViewConfig viewConfig;

	@Test
	@DisplayName("Verify content of ViewConfig")
	void when_ViewConfigIsInjected_ViewSpecificationsCanBeConsulted() {
		assertEquals(3, viewConfig.getViews().size());

		ViewSpecification firstViewSpecification = viewConfig.getViews().get(0);
		assertEquals("firstView", firstViewSpecification.getName());
		assertEquals(2, firstViewSpecification.getFragmentations().size());
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(0), "geospatial",
				Map.of("maxZoomLevel", "15", "fragmenterProperty", "http://www.opengis.net/ont/geosparql#asWKT"));
		verifyViewSpecification(firstViewSpecification.getFragmentations().get(1), "timebased",
				Map.of("memberLimit", "5"));
		assertEquals(1, firstViewSpecification.getRetentionConfigs().size());
		RetentionConfig retentionConfig = firstViewSpecification.getRetentionConfigs().get(0);
		verifyRetentionPolicy(retentionConfig);

		ViewSpecification secondViewSpecification = viewConfig.getViews().get(1);
		assertEquals("secondView", secondViewSpecification.getName());
		assertEquals(1, secondViewSpecification.getFragmentations().size());
		assertEquals(List.of(), secondViewSpecification.getRetentionConfigs());
		verifyViewSpecification(secondViewSpecification.getFragmentations().get(0), "timebased",
				Map.of("memberLimit", "3"));

		ViewSpecification defaultViewSpecification = viewConfig.getViews().get(2);
		assertEquals("by-page", defaultViewSpecification.getName());
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
			Map<String, String> maxZoomLevel) {
		assertEquals(fragmentationName, fragmentationConfig.getName());
		assertEquals(new ConfigProperties(maxZoomLevel), fragmentationConfig.getProperties());
	}
}
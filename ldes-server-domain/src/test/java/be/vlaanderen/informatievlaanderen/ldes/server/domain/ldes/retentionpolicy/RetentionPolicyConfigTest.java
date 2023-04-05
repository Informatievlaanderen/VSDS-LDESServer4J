package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RetentionPolicyConfigTest {

	@Test
	void when_TimeBasedRetentionPolicyIsDefinedInConfig_ItIsAddedToMap() {
		ViewConfig viewConfig = new ViewConfig();
		viewConfig.setViews(List.of(getFirstViewSpecification("timebased")));

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		Map<String, List<RetentionPolicy>> retentionPolicyMap = retentionPolicyConfig.retentionPolicyMap(viewConfig);
		assertEquals(1, retentionPolicyMap.size());
		assertEquals(1, retentionPolicyMap.get("firstView").size());
		assertTrue(retentionPolicyMap.get("firstView").get(0) instanceof TimeBasedRetentionPolicy);
	}

	@Test
	void when_OtherRetentionPoliciesAreDefinedInConfig_IllegalArgumentExceptionIsThrown() {
		ViewConfig viewConfig = new ViewConfig();
		viewConfig.setViews(List.of(getFirstViewSpecification("other")));

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyConfig.retentionPolicyMap(viewConfig));
		assertEquals("Invalid retention Policy: other", exception.getMessage());
	}

	private ViewSpecification getFirstViewSpecification(String policiyName) {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName("firstView");
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName(policiyName);
		retentionConfig.setConfig(Map.of("durationInSeconds", "100"));
		viewSpecification.setRetentionPolicies(List.of(retentionConfig));
		return viewSpecification;
	}
}
package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetentionPolicyConfigTest {

	@Test
	void when_TimeBasedRetentionPolicyIsDefinedInConfig_ItIsAddedToMap() {
		LdesConfig ldesConfig = getLdesConfig("timebased");

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		Map<String, List<RetentionPolicy>> retentionPolicyMap = retentionPolicyConfig.retentionPolicyMap(ldesConfig);
		assertEquals(1, retentionPolicyMap.size());
		assertEquals(1, retentionPolicyMap.get("parcels/firstView").size());
		assertTrue(retentionPolicyMap.get("parcels/firstView").get(0) instanceof TimeBasedRetentionPolicy);
	}

	@Test
	void when_OtherRetentionPoliciesAreDefinedInConfig_IllegalArgumentExceptionIsThrown() {
		LdesConfig ldesConfig = getLdesConfig("other");

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyConfig.retentionPolicyMap(ldesConfig));
		assertEquals("Invalid retention Policy: other", exception.getMessage());
	}

	private LdesConfig getLdesConfig(String policyName) {
		LdesConfig ldesConfig = new LdesConfig();
		LdesSpecification ldesSpecification = getFirstLdesSpecification(policyName);
		ldesConfig.setCollections(List.of(ldesSpecification));
		return ldesConfig;
	}

	private LdesSpecification getFirstLdesSpecification(String policyName) {
		LdesSpecification ldesSpecification = new LdesSpecification();
		ldesSpecification.setHostName("http://localhost:8080");
		ldesSpecification.setCollectionName("parcels");
		ldesSpecification.setMemberType("https://vlaanderen.be/implementatiemodel/gebouwenregister#Perceel");
		ldesSpecification.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesSpecification.setViews(List.of(getFirstViewSpecification(policyName)));
		return ldesSpecification;
	}

	private ViewSpecification getFirstViewSpecification(String policyName) {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName("firstView");
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName(policyName);
		retentionConfig.setConfig(Map.of("duration", "PT1M"));
		viewSpecification.setRetentionPolicies(List.of(retentionConfig));
		return viewSpecification;
	}
}
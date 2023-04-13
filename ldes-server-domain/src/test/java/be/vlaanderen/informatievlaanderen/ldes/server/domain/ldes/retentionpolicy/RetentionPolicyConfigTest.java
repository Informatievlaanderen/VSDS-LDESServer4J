package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
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
		AppConfig appConfig = getLdesConfig("timebased");

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		Map<String, List<RetentionPolicy>> retentionPolicyMap = retentionPolicyConfig.retentionPolicyMap(appConfig);
		assertEquals(1, retentionPolicyMap.size());
		assertEquals(1, retentionPolicyMap.get("parcels/firstView").size());
		assertTrue(retentionPolicyMap.get("parcels/firstView").get(0) instanceof TimeBasedRetentionPolicy);
	}

	@Test
	void when_OtherRetentionPoliciesAreDefinedInConfig_IllegalArgumentExceptionIsThrown() {
		AppConfig appConfig = getLdesConfig("other");

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyConfig.retentionPolicyMap(appConfig));
		assertEquals("Invalid retention Policy: other", exception.getMessage());
	}

	private AppConfig getLdesConfig(String policyName) {
		AppConfig appConfig = new AppConfig();
		LdesConfig ldesConfig = getFirstLdesSpecification(policyName);
		appConfig.setCollections(List.of(ldesConfig));
		return appConfig;
	}

	private LdesConfig getFirstLdesSpecification(String policyName) {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("http://localhost:8080");
		ldesConfig.setCollectionName("parcels");
		ldesConfig.setMemberType("https://vlaanderen.be/implementatiemodel/gebouwenregister#Perceel");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setViews(List.of(getFirstViewSpecification(policyName)));
		return ldesConfig;
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
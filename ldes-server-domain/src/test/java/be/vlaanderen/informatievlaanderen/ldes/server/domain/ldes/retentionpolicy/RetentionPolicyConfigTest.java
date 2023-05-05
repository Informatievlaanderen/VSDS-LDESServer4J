package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RetentionPolicyConfigTest {

	private final RetentionPolicyCreator retentionPolicyCreator = new RetentionPolicyCreatorImpl();

	@Test
	void when_TimeBasedRetentionPolicyIsDefinedInConfig_ItIsAddedToMap() {
		AppConfig appConfig = getLdesConfig("timebased");

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		Map<ViewName, List<RetentionPolicy>> retentionPolicyMap = retentionPolicyConfig.retentionPolicyMap(appConfig,
				retentionPolicyCreator);
		assertEquals(1, retentionPolicyMap.size());
		assertEquals(1, retentionPolicyMap.get(ViewName.fromString("parcels/firstView")).size());
		assertTrue(retentionPolicyMap.get(ViewName.fromString("parcels/firstView"))
				.get(0) instanceof TimeBasedRetentionPolicy);
	}

	@Test
	@Disabled("To be enabled again once timebased retention is finished")
	void when_OtherRetentionPoliciesAreDefinedInConfig_IllegalArgumentExceptionIsThrown() {
		AppConfig appConfig = getLdesConfig("other");

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyConfig.retentionPolicyMap(appConfig, retentionPolicyCreator));
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
		ldesConfig.setCollectionName("parcels");
		ldesConfig.setMemberType("https://vlaanderen.be/implementatiemodel/gebouwenregister#Perceel");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setViews(List.of(getFirstViewSpecification(policyName, ldesConfig.getCollectionName())));
		return ldesConfig;
	}

	private ViewSpecification getFirstViewSpecification(String policyName, String collectionName) {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName(new ViewName(collectionName, "firstView"));
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName(policyName);
		retentionConfig.setConfig(Map.of("duration", "PT1M"));
		viewSpecification.setRetentionPolicies(List.of(retentionConfig));
		return viewSpecification;
	}
}